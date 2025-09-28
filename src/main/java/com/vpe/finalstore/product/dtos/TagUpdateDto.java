package com.vpe.finalstore.product.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagUpdateDto {
    @NotBlank
    private String name;
}
