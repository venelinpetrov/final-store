package com.vpe.finalstore.tags.dtos;

import com.vpe.finalstore.product.dtos.ProductSummaryDto;
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
