package com.vpe.finalstore.product.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.product.dtos.ProductCreateDto;
import com.vpe.finalstore.product.dtos.ProductUpdateDto;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.repositories.*;
import com.vpe.finalstore.product.repositories.TagRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class ProductService {
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductImageAssignmentRepository imageAssignmentRepository;
    private final ProductImageRepository imageRepository;
    private final ProductVariantService variantService;

    @Transactional
    public Product createProduct(ProductCreateDto req) {
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

                imageAssignmentRepository.save(productImageAssignmentEntity);
            });

        if (req.getVariants() != null) {
            for (var variantReq : req.getVariants()) {
                variantService.addVariant(product, variantReq);
            }
        }

        return product;
    }

    @Transactional
    public Product updateProduct(ProductUpdateDto req, Integer productId) {
        var product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product not found with id " + productId));

        // Simple fields
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setIsArchived(req.getIsArchived());

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

        return productRepository.save(product);
    }

    public Page<Product> getProductsByAnyTagNames(Set<String> tagNames, Pageable pageable) {
        Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        return productRepository.getByAnyTagsIn(tags, pageable);
    }

    public Page<Product> getProductsByAllTagNames(Set<String> tagNames, Pageable pageable) {
        Set<Tag> tags = tagRepository.findByNameIn(tagNames);

        long tagCount = tags.size();

        if (tagCount != tagNames.size()) {
            return Page.empty(pageable);
        }

        return productRepository.getByAllTagsIn(tags, tagCount, pageable);

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

        imageAssignmentRepository.saveAll(assignments);
    }

    public void unassignImages(List<Integer> imageIds, Product product) {
        var assignments = imageIds.stream()
            .map(imageId -> imageAssignmentRepository.findAssignment(imageId, product.getProductId())
                .orElseThrow(
                    () -> new NotFoundException("Image with ID %d not assigned to product %d".formatted(imageId, product.getProductId()))
                )
            )
            .toList();

        imageAssignmentRepository.deleteAll(assignments);
    }
}
