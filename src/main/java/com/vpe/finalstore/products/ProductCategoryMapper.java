package com.vpe.finalstore.products;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategoryDto toDto(ProductCategoryView categoryView);
    List<ProductCategoryDto> toDto(List<ProductCategoryView> views);
}
