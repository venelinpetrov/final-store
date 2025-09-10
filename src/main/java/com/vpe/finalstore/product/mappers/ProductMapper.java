package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.dtos.ProductSummaryDto;
import com.vpe.finalstore.product.entities.Product;
import com.vpe.finalstore.product.entities.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "brandId", source = "brand.brandId")
    @Mapping(target = "categoryIds", source = "categories")
    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);

    List<ProductSummaryDto> toSummaryDto(List<Product> products);

    default Set<Integer> mapCategories(Set<ProductCategory> categories) {
        if (categories == null) {
            return Set.of();
        }
        return categories.stream()
            .map(ProductCategory::getCategoryId)
            .collect(Collectors.toSet());
    }
}
