package com.vpe.finalstore.cart.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class CartDto {
   private Integer cartId;
   private String sessionId;
   private LocalDateTime createdAt;
   private LocalDateTime updatedAt;
   private Set<CartItemDto> cartItems;
}