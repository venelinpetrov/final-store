package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class TagDto {
    private Integer tagId;
    private String name;
    private Set<ProductSummaryDto> products;
}
