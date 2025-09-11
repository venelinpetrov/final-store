package com.vpe.finalstore.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductImageDto {
    private Integer imageId;
    private String link;
    private String altText;
    private Boolean isPrimary;
}
