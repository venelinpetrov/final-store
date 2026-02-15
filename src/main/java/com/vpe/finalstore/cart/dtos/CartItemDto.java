package com.vpe.finalstore.cart.dtos;

import com.vpe.finalstore.product.dtos.ProductVariantDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartItemDto {
    private ProductVariantDto variant;
    private short quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}