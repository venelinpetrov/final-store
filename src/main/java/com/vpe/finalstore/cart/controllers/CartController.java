package com.vpe.finalstore.cart.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.mappers.CartMapper;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Controller
@RequestMapping("/api/carts")
class CartController {
    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) {
        var cart = cartService.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart with UUID: " + cartId + " not found"));

        return cartMapper.toDto(cart);
    }

    @PostMapping
    ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cart = cartService.createCart();
        var cartDto = cartMapper.toDto(cart);
        var uri = uriBuilder.path("/carts/{cart_id}").buildAndExpand(cartDto.getCartId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }
    // Add to cart

    // Get cart

    // Delete cart item

    // Exception handlers
}
