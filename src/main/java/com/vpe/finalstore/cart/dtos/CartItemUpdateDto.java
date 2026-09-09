package com.vpe.finalstore.cart.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemUpdateDto {
    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
