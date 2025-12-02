package com.vpe.finalstore.cart.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemAddDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.dtos.CartItemUpdateDto;
import com.vpe.finalstore.cart.mappers.CartMapper;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.exceptions.NotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
        var uri = uriBuilder.path("/carts/{cart_id}")
            .buildAndExpand(cartDto.getCartId())
            .toUri();

        return ResponseEntity.created(uri)
            .body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
        @PathVariable(name = "cartId") UUID cartId,
        @RequestBody CartItemAddDto body) {

        var cartItem = cartService.addToCart(cartId, body.getVariantId());
        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(cartItemDto);
    }

    @PutMapping("/{cartId}/items/{variantId}")
    public ResponseEntity<CartItemDto> updateCart(
        @PathVariable("cartId") UUID cartId,
        @PathVariable("variantId") Integer variantId,
        @Valid @RequestBody CartItemUpdateDto body
    ) {
        var cartItem = cartService.updateCartItem(cartId, variantId, body.getQuantity());

        return ResponseEntity.ok(cartMapper.toDto(cartItem));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{cartId}/items/{variantId}")
    public void deleteCartItem(
        @PathVariable("cartId") UUID cartId,
        @PathVariable("variantId") Integer variantId
    ) {
        cartService.deleteCartItem(cartId, variantId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{cartId}/items")
    public void clearCart(@PathVariable("cartId") UUID cartId) {
        cartService.clearCart(cartId);
    }
}
