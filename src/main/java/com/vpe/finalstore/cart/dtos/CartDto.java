package com.vpe.finalstore.cart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

@Getter
@Setter
public class CartDto {
   private UUID cartId;
   private UUID sessionId;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private LinkedHashSet<CartItemDto> cartItems;
}