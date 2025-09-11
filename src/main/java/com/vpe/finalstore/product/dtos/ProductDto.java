package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ProductDto {
    private Integer productId;
    private String name;
    private String description;
    private Set<Integer> categoryIds;
    private Integer brandId;
    private Set<TagSummaryDto> tags;
    private Set<ProductImageDto> images;
}
