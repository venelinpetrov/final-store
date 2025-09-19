package com.vpe.finalstore.product.services;

import com.vpe.finalstore.product.dtos.ProductCreateDto;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.repositories.*;
import com.vpe.finalstore.product.repositories.TagRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProductService {
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantOptionRepository optionRepository;
    private final ProductVariantOptionValueRepository optionValueRepository;
    private final ProductImageAssignmentRepository productImageAssignmentRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantImageAssignmentRepository productVariantImageAssignmentRepository;

    @Transactional
    public Product createProduct(ProductCreateDto req) {
        var brand = brandRepository.findById(req.getBrandId()).orElseThrow();
        var categories = new HashSet<>(productCategoryRepository.findAllById(req.getCategoryIds()));
        var tags = Set.copyOf(tagRepository.findAllById(req.getTags()));
        var product = Product.builder()
            .name(req.getName())
            .description(req.getDescription())
            .brand(brand)
            .categories(categories)
            .tags(tags)
            .build();

        productRepository.save(product);

        req.getImages()
            .forEach(image -> {
                var imageEntity = new ProductImage(image.getLink(), image.getAltText());
                productImageRepository.save(imageEntity);

                var productImageAssignmentEntity = new ProductImageAssignment(
                    product,
                    imageEntity,
                    image.getIsPrimary()
                );

                productImageAssignmentRepository.save(productImageAssignmentEntity);
            });

        if (req.getVariants() != null) {
            for (var variantReq : req.getVariants()) {
                var variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku(variantReq.getSku());
                variant.setUnitPrice(variantReq.getUnitPrice());
                variant.setQuantityInStock(variantReq.getQuantityInStock());

                if (variantReq.getOptions() != null) {
                    for (var optionReq : variantReq.getOptions()) {
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

                variantRepository.save(variant);

                variantReq.getImages()
                    .forEach(image -> {
                        var imageEntity = new ProductImage(image.getLink(), image.getAltText());
                        productImageRepository.save(imageEntity);

                        var productVariantImageAssignmentEntity = new ProductVariantImageAssignment(
                            variant,
                            imageEntity,
                            image.getIsPrimary()
                        );

                        productVariantImageAssignmentRepository.save(productVariantImageAssignmentEntity);
                    });
            }
        }

        return product;
    }

    public Page<Product> getProductsByTagNames(Set<String> tagNames, Pageable pageable) {
        Set<Tag> tags = tagRepository.findByNameIn(tagNames);
        return productRepository.getByTagsIn(tags, pageable);
    }
}
