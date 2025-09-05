package com.vpe.finalstore.products;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.categoryId")
    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);
}
