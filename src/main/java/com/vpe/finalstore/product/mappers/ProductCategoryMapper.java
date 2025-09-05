package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.repositories.ProductCategoryView;
import com.vpe.finalstore.product.dtos.ProductCategoryDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {
    ProductCategoryDto toDto(ProductCategoryView categoryView);
    List<ProductCategoryDto> toDto(List<ProductCategoryView> views);
}
