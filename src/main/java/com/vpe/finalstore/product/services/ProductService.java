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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@Service
public class ProductService {
    private final BrandRepository brandRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageAssignmentRepository productImageAssignmentRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantService variantService;

    @Transactional
    public Product createProduct(ProductCreateDto req) {
        var brand = brandRepository.findById(req.getBrandId()).orElseThrow(() -> new NotFoundException("Brand does not exist"));
        var categories = new HashSet<>(productCategoryRepository.findAllById(req.getCategoryIds()));
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
                productImageRepository.save(imageEntity);

                var productImageAssignmentEntity = new ProductImageAssignment(
                    product,
                    imageEntity,
                    imageDto.getIsPrimary()
                );

                productImageAssignmentRepository.save(productImageAssignmentEntity);
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
        var categories = new LinkedHashSet<>(productCategoryRepository.findAllById(req.getCategoryIds()));
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
}
