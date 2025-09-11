package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProductCreateDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters long")
    private String name;

    @NotNull
    private Integer brandId;

    @NotNull
    private List<Integer> categoryIds;

    private String description;

    private List<Integer> tags;

    private List<ProductVariantCreateDto> variants;

    private Set<ProductImageCreateDto> images;
}
