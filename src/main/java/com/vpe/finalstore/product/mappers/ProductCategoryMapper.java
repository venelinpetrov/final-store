package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.entities.ProductCategory;
import com.vpe.finalstore.product.repositories.ProductCategoryView;
import com.vpe.finalstore.product.dtos.ProductCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategoryDto toDto(ProductCategoryView categoryView);
    @Mapping(target="parentCategoryId", source = "parentCategory.categoryId")
    ProductCategoryDto toDto(ProductCategory category);
    List<ProductCategoryDto> toDto(List<ProductCategoryView> views);
}
