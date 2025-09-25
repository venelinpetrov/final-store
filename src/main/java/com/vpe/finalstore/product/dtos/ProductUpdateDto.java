package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpdateDto {
    @NotNull
    private List<Integer> categoryIds;

    @NotNull
    private String description;

    @NotNull
    private List<Integer> tags;

    @NotNull
    private Boolean isArchived;
}
