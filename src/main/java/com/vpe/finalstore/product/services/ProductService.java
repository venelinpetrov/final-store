package com.vpe.finalstore.product.services;

import com.vpe.finalstore.discount.entities.Discount;
import com.vpe.finalstore.discount.enums.DiscountConditionType;
import com.vpe.finalstore.discount.enums.DiscountType;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.inventory.dtos.InventoryMovementCreateDto;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import com.vpe.finalstore.inventory.services.InventoryMovementService;
import com.vpe.finalstore.product.dtos.ActiveDiscountDto;
import com.vpe.finalstore.product.dtos.ProductCreateDto;
import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.dtos.ProductSummaryDto;
import com.vpe.finalstore.product.dtos.ProductUpdateDto;
import com.vpe.finalstore.product.dtos.ProductVariantCreateDto;
import com.vpe.finalstore.product.dtos.ProductVariantDto;
import com.vpe.finalstore.product.dtos.ProductVariantUpdateDto;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.mappers.ProductMapper;
import com.vpe.finalstore.product.mappers.ProductVariantMapper;
import com.vpe.finalstore.product.repositories.*;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductImageAssignmentRepository productImageAssignmentRepository;
    private final ProductVariantImageAssignmentRepository productVariantImageAssignmentRepository;
    private final ProductImageRepository imageRepository;
    private final ProductMapper productMapper;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantOptionRepository optionRepository;
    private final ProductVariantOptionValueRepository optionValueRepository;
    private final InventoryMovementService inventoryMovementService;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final InventoryLevelRepository inventoryLevelRepository;
    private final DiscountRepository discountRepository;

    public Page<ProductSummaryDto> getProducts(Integer brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (brandId != null) {
            products = productRepository.findProductsByBrandBrandIdAndIsArchivedIsFalse(brandId, pageable);
        } else {
            products = productRepository.getAllWithTags(pageable);
        }

        List<ProductSummaryDto> dtos = productMapper.toSummaryDto(products.getContent());
        var discounts = getVariantsToDiscountsMap();

        dtos.forEach(product -> product.getVariants().forEach((variant)-> setDiscountToVariantDto(variant, discounts)));

        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @Transactional
    public ProductDto createProduct(ProductCreateDto req) {
        var brand = brandRepository.findById(req.getBrandId()).orElseThrow(() -> new NotFoundException("Brand does not exist"));
        var categories = new HashSet<>(categoryRepository.findAllById(req.getCategoryIds()));
        var tags = Set.copyOf(tagRepository.findAllById(req.getTags()));
        var product = Product.builder()
            .name(req.getName())
            .description(req.getDescription())
            .brand(brand)
            .categories(categories)
            .tags(tags)
            .isArchived(req.getIsArchived())
            .taxCode(req.getTaxCode())
            .build();

        productRepository.save(product);

        req.getImages()
            .forEach(imageDto -> {
                var imageEntity = new ProductImage(imageDto.getLink(), imageDto.getAltText());
                imageRepository.save(imageEntity);

                var productImageAssignmentEntity = new ProductImageAssignment(
                    product,
                    imageEntity,
                    imageDto.getIsPrimary()
                );

                productImageAssignmentRepository.save(productImageAssignmentEntity);
            });

        if (req.getVariants() != null) {
            for (var variantReq : req.getVariants()) {
                createVariant(product, variantReq);
            }
        }

        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDto updateProduct(ProductUpdateDto req, Integer productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found with id " + productId));

        // Simple fields
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setIsArchived(req.getIsArchived());
        product.setTaxCode(req.getTaxCode());

        // Brand
        var brandId = req.getBrandId();
        var brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new NotFoundException("Brand not found with id " + brandId));
        product.setBrand(brand);

        // Categories
        var categories = new LinkedHashSet<>(categoryRepository.findAllById(req.getCategoryIds()));
        if (categories.size() != req.getCategoryIds().size()) {
            throw new NotFoundException("One or more categories not found");
        }
        product.setCategories(categories);

        // Tags
        if (req.getTags() != null) {
            var tags = new LinkedHashSet<>(tagRepository.findAllById(req.getTags()));
            if (tags.size() != req.getTags().size()) {
                throw new NotFoundException("One or more tags not found");
            }
            product.setTags(tags);
        }

        var updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    public Page<ProductDto> getProductsByAnyTagNames(Set<String> tagNames, Pageable pageable) {
        Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        return productRepository.getByAnyTagsIn(tags, pageable)
            .map(productMapper::toDto);
    }

    public Page<ProductDto> getProductsByAllTagNames(Set<String> tagNames, Pageable pageable) {
        Set<Tag> tags = tagRepository.findByNameIn(tagNames);

        long tagCount = tags.size();

        if (tagCount != tagNames.size()) {
            return Page.empty(pageable);
        }

        return productRepository.getByAllTagsIn(tags, tagCount, pageable)
            .map(productMapper::toDto);
    }

    @Transactional
    public void archiveProduct(Integer productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setIsArchived(true);
        product.getVariants().forEach(variant -> variant.setIsArchived(true));

        productRepository.save(product);
    }

    @Transactional
    public void unarchiveProduct(Integer productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setIsArchived(false);

        product.getVariants().forEach(variant -> variant.setIsArchived(false));

        productRepository.save(product);
    }

    @Transactional
    public void assignImages(List<Integer> imageIds, Product product) {
        List<Integer> distinctImageIds = imageIds.stream()
            .distinct()
            .toList();

        var images = imageRepository.findAllById(distinctImageIds);
        if (images.size() != distinctImageIds.size()) {
            throw new NotFoundException("Some images were not found");
        }

        List<ProductImageAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < distinctImageIds.size(); i++) {
            var imageId = distinctImageIds.get(i);
            var image = images.stream()
                .filter(img -> img.getImageId().equals(imageId))
                .findFirst()
                .orElseThrow();

            boolean isPrimary = (i == 0);
            assignments.add(new ProductImageAssignment(product, image, isPrimary));
        }

        productImageAssignmentRepository.saveAll(assignments);
    }

    public void unassignImages(List<Integer> imageIds, Product product) {
        var assignments = imageIds.stream()
            .map(imageId -> productImageAssignmentRepository.findAssignment(imageId, product.getProductId())
                .orElseThrow(
                    () -> new NotFoundException("Image with ID %d not assigned to product %d".formatted(imageId, product.getProductId()))
                )
            )
            .toList();

        productImageAssignmentRepository.deleteAll(assignments);
    }

    public ProductVariantDto getVariantById(Integer variantId) {
        var variant = variantRepository.findByVariantId(variantId)
            .orElseThrow(VariantNotFoundException::new);

        var dto = productVariantMapper.toDto(variant);
        enrichVariantDto(dto);
        return dto;
    }

    public List<ProductVariantDto> getVariantsByProductId(Integer productId) {
        var variants = productVariantRepository.findProductVariantsByProductProductIdAndIsArchivedIsFalse(productId)
            .orElseThrow(() -> new NotFoundException("Product not found"));

        var dtos = productVariantMapper.toDto(variants);
        dtos.forEach(this::enrichVariantDto);
        return dtos;
    }

    @Transactional
    public void archiveVariant(Integer variantId) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setIsArchived(true);

        variantRepository.save(variant);
    }

    @Transactional
    public void unarchiveVariant(Integer variantId) {
        var variant = variantRepository.findByVariantIdAndIsArchivedIsTrue(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setIsArchived(false);

        variantRepository.save(variant);
    }

    @Transactional
    public void updateVariant(Integer variantId, ProductVariantUpdateDto req) {
        var variant = variantRepository.findById(variantId)
            .orElseThrow(VariantNotFoundException::new);

        variant.setUnitPrice(req.getUnitPrice());

        variantRepository.save(variant);
    }

    @Transactional
    public void deleteVariant(Integer variantId) {
        variantRepository.deleteById(variantId);
    }

    @Transactional
    public ProductVariant createVariant(Product product, ProductVariantCreateDto variantDto) {
        var variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(variantDto.getSku());
        variant.setUnitPrice(variantDto.getUnitPrice());
        variant.setIsArchived(variantDto.getIsArchived());

        if (variantDto.getOptions() != null) {
            for (var optionReq : variantDto.getOptions()) {
                var option = optionRepository.findByNameIgnoreCase(optionReq.getName())
                    .orElseGet(() -> {
                        var o = new ProductVariantOption();
                        o.setName(optionReq.getName());

                        return optionRepository.save(o);
                    });
                var value = optionValueRepository.findByOptionAndValue(option, optionReq.getValue())
                    .orElseGet(() -> {
                        var v = new ProductVariantOptionValue();
                        v.setOption(option);
                        v.setValue(optionReq.getValue());

                        return optionValueRepository.save(v);
                    });

                var assignment = new ProductVariantOptionAssignment(variant, value);

                variant.getOptionAssignments().add(assignment);
            }
        }

        for (var reqImage : variantDto.getImages()) {
            var image = imageRepository.findByLink(reqImage.getLink())
                .orElseGet(() -> imageRepository.save(
                    new ProductImage(reqImage.getLink(), reqImage.getAltText())
                ));

            var assignment = new ProductVariantImageAssignment(variant, image, reqImage.getIsPrimary());
            variant.getImages().add(assignment);
        }

        variant = variantRepository.save(variant);

        var initMovement = new InventoryMovementCreateDto();
        initMovement.setVariantId(variant.getVariantId());
        initMovement.setMovementType(MovementType.ADJUSTMENT);
        initMovement.setQuantity(variantDto.getQuantityInStock());
        initMovement.setReason("Initialize stock for new variant");

        inventoryMovementService.createMovement(initMovement);

        return variantRepository.save(variant);
    }

    @Transactional
    public void assignImages(List<Integer> imageIds, ProductVariant variant) {
        List<Integer> distinctImageIds = imageIds.stream()
            .distinct()
            .toList();

        var images = imageRepository.findAllById(distinctImageIds);
        if (images.size() != distinctImageIds.size()) {
            throw new NotFoundException("Some images were not found");
        }

        List<ProductVariantImageAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < distinctImageIds.size(); i++) {
            var imageId = distinctImageIds.get(i);
            var image = images.stream()
                .filter(img -> img.getImageId().equals(imageId))
                .findFirst()
                .orElseThrow();

            boolean isPrimary = (i == 0);
            assignments.add(new ProductVariantImageAssignment(variant, image, isPrimary));
        }

        productVariantImageAssignmentRepository.saveAll(assignments);
    }

    public void unassignImages(List<Integer> imageIds, ProductVariant variant) {
        var assignments = imageIds.stream()
            .map(imageId -> productImageAssignmentRepository.findAssignment(imageId, variant.getVariantId())
                .orElseThrow(
                    () -> new NotFoundException("Image with ID %d not assigned to variant %d".formatted(imageId, variant.getVariantId()))
                )
            )
            .toList();

        productImageAssignmentRepository.deleteAll(assignments);
    }

    private void enrichVariantDto(ProductVariantDto dto) {
        if (dto == null || dto.getVariantId() == null) {
            return;
        }

        var inventory = inventoryLevelRepository.findByVariantVariantId(dto.getVariantId());
        dto.setQuantityInStock(inventory.map(inv -> inv.getQuantityInStock()).orElse(0));

        var discountOpt = discountRepository.findActiveDiscountForVariant(dto.getVariantId());
        if (discountOpt.isPresent()) {
            var discount = discountOpt.get();

            // Map PERCENTAGE and FIXED discounts
            if (discount.getDiscountType() == DiscountType.PERCENTAGE ||
                    discount.getDiscountType() == DiscountType.FIXED) {
                dto.setDiscount(new ActiveDiscountDto(
                        discount.getDiscountType(),
                        discount.getValue(),
                        discount.getValidUntil()));
            }
        }
    }

    private Map<Integer, Discount> getVariantsToDiscountsMap() {
        var discounts = discountRepository.findActiveVariantDiscounts();

        Function<Discount, Integer> keyMapper = (discount) -> discount.getDiscountConditions().stream()
            .filter(x -> x.getConditionType().equals(DiscountConditionType.VARIANT))
            .findFirst().get().getIntValue();

        Function<Discount, Discount> valueMapper = Function.identity();

        return discounts.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    private void setDiscountToVariantDto(ProductVariantDto variant, Map<Integer, Discount> discounts) {

        var discountEntity = discounts.getOrDefault(variant.getVariantId(), null);

        if (discountEntity != null) {
            var activeDiscount = new ActiveDiscountDto(
                discountEntity.getDiscountType(),
                discountEntity.getValue(),
                discountEntity.getValidUntil());
            variant.setDiscount(activeDiscount);
        }
    }
}
