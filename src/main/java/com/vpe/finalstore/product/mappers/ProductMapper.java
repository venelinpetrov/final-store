package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.*;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.entities.ProductCategory;
import com.vpe.finalstore.product.entities.ProductImageAssignment;
import com.vpe.finalstore.product.entities.ProductVariantImageAssignment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    uses = ProductVariantMapper.class
)
public interface ProductMapper {
    @Mapping(target = "brandId", source = "brand.brandId")
    @Mapping(target = "categoryIds", source = "categories")
    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);

    @Mapping(target = "imageId", source = "image.imageId")
    @Mapping(target = "link", source = "image.link")
    @Mapping(target = "altText", source = "image.altText")
    ProductImageDto toDto(ProductVariantImageAssignment assignment);

    List<ProductSummaryDto> toSummaryDto(List<Product> products);

    default Set<Integer> mapCategories(Set<ProductCategory> categories) {
        if (categories == null) {
            return Set.of();
        }
        return categories.stream()
            .map(ProductCategory::getCategoryId)
            .collect(Collectors.toSet());
    }

    default Set<ProductImageDto> mapProductImages(Set<ProductImageAssignment> images) {
        if (images == null) {
            return Set.of();
        }
        return images.stream()
            .map((imageAssignment) -> new ProductImageDto(
                imageAssignment.getImage().getImageId(),
                imageAssignment.getImage().getLink(),
                imageAssignment.getImage().getAltText(),
                imageAssignment.getIsPrimary()
            ))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
