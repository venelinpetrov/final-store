package com.vpe.finalstore.product.mappers;

import com.vpe.finalstore.product.dtos.ProductDto;
import com.vpe.finalstore.product.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryId", source = "category.categoryId")
    @Mapping(target = "brandId", source = "brand.brandId")
    ProductDto toDto(Product product);

    List<ProductDto> toDto(List<Product> products);
}
