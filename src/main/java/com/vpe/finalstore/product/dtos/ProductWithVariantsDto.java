package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductWithVariantsDto {
    private ProductDto product;
    private List<ProductVariantDto> variants;
}
