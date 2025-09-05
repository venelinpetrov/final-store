package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDto {
    private Integer productId;
    private String name;
    private String description;
    private Integer categoryId;
    private Integer brandId;
}
