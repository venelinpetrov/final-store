package com.vpe.finalstore.cart.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemUpdateDto {
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than zero")
    @Max(value = 10_000, message = "Quantity must be less than or equal to 10_000")
    private short quantity;
}
