package com.vpe.finalstore.product.services;

import com.vpe.finalstore.product.dtos.ProductCreateRequestDto;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.repositories.*;
import com.vpe.finalstore.tags.repositories.TagRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    @Transactional
    public Product createProduct(ProductCreateRequestDto req) {
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
            }
        }

        return product;
    }
}
