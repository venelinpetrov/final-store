package com.vpe.finalstore.cart.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemAddDto {
    @NotNull
    private Integer variantId;
}
