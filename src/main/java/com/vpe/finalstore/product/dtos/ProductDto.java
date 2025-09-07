package com.vpe.finalstore.product.dtos;

import com.vpe.finalstore.tags.dtos.TagSummaryDto;
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
    private Integer categoryId;
    private Integer brandId;
    private Set<TagSummaryDto> tags;
}
