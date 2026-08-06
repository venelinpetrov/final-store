package com.vpe.finalstore.product.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSummaryDto {
    private Integer productId;
    private String name;
    private String description;
    private Set<ProductVariantSummaryDto> variants;
}
