package com.vpe.finalstore.cart.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemAddDto;
import com.vpe.finalstore.cart.dtos.CartItemDto;
import com.vpe.finalstore.cart.dtos.CartItemUpdateDto;
import com.vpe.finalstore.cart.mappers.CartMapper;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.users.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;

    @Operation(
        summary = "Get cart by UUID"
    )
    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) {
        var cart = cartService.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart with UUID: " + cartId + " not found"));

        return cartMapper.toDto(cart);
    }

    @Operation(
        summary = "Create cart"
    )
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

    @Operation(
        summary = "Add an item to the cart"
    )
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(@PathVariable UUID cartId, @RequestBody CartItemAddDto body) {
        var cartItem = cartService.addToCart(cartId, body.getVariantId());
        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(cartItemDto);
    }

    @Operation(
        summary = "Update cart"
    )
    @PutMapping("/{cartId}/items/{variantId}")
    public ResponseEntity<CartItemDto> updateCart(
        @PathVariable UUID cartId,
        @PathVariable Integer variantId,
        @Valid @RequestBody CartItemUpdateDto body
    ) {
        var cartItem = cartService.updateCartItem(cartId, variantId, body.getQuantity());

        return ResponseEntity.ok(cartMapper.toDto(cartItem));
    }

    @Operation(
        summary = "Delete cart item"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{cartId}/items/{variantId}")
    public void deleteCartItem(@PathVariable UUID cartId, @PathVariable Integer variantId) {
        cartService.deleteCartItem(cartId, variantId);
    }

    @Operation(
        summary = "Empty/clear cart"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{cartId}/items")
    public void clearCart(@PathVariable UUID cartId) {
        cartService.clearCart(cartId);
    }

    @Operation(
        summary = "Get cart by session ID (for anonymous users)"
    )
    @GetMapping("/session/{sessionId}")
    public CartDto getCartBySession(@PathVariable UUID sessionId) {
        var cart = cartService.getCartBySessionId(sessionId)
            .orElseThrow(() -> new NotFoundException("Cart with session ID: " + sessionId + " not found"));

        return cartMapper.toDto(cart);
    }

    @Operation(
        summary = "Get cart for current logged-in user"
    )
    @GetMapping("/my-cart")
    public CartDto getMyCart(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        var cart = cartService.getCartByCustomerId(user.getCustomer().getCustomerId())
            .orElseThrow(() -> new NotFoundException("Cart not found for customer"));

        return cartMapper.toDto(cart);
    }

    @Operation(
        summary = "Associate anonymous cart with logged-in user (after login)"
    )
    @PostMapping("/associate")
    public CartDto associateCart(
        @RequestParam UUID sessionId,
        Authentication authentication
    ) {
        Integer userId = (Integer) authentication.getPrincipal();
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        var cart = cartService.associateCartWithCustomer(sessionId, user.getCustomer().getCustomerId());

        return cartMapper.toDto(cart);
    }
}
