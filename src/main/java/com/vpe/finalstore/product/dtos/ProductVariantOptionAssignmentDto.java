package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductVariantOptionAssignmentDto {
    private Integer optionId;
    private String optionName;
    private Integer valueId;
    private String value;
}