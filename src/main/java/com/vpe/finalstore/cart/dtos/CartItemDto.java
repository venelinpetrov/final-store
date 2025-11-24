package com.vpe.finalstore.cart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartItemDto {
    private Integer itemId;
    private Integer variantId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}