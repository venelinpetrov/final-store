package com.vpe.finalstore.cart.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemAddDto;
import com.vpe.finalstore.cart.dtos.CartItemUpdateDto;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.common.services.CookieService;
import com.vpe.finalstore.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    private final CookieService cookieService;

    private static final String CART_COOKIE_NAME = "finalstore_cartSessionId";
    private static final String CART_COOKIE_PATH = "/api/carts";

    @Operation(
        summary = "Get cart by UUID"
    )
    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) {
        return cartService.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart with UUID: " + cartId + " not found"));
    }

    @Operation(
        summary = "Create cart"
    )
    @PostMapping
    public ResponseEntity<CartDto> getOrCreateCart(
        @CookieValue(name = CART_COOKIE_NAME, required = false) String sessionId,
        HttpServletResponse response
    ) {
        var cartDto = cartService.getOrCreateCart(sessionId);

        if (sessionId == null || sessionId.isBlank()) {
            response.addCookie(
                getCookie(cartDto.getSessionId().toString())
            );
        }

        return ResponseEntity.ok(cartDto);
    }

    @Operation(
        summary = "Add an item to the cart"
    )
    @PostMapping("/{cartId}/items")
    public ResponseEntity<Void> addToCart(@PathVariable UUID cartId, @RequestBody CartItemAddDto body) {
        cartService.addToCart(cartId, body.getVariantId(), body.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
        summary = "Update cart"
    )
    @PutMapping("/{cartId}/items/{variantId}")
    public ResponseEntity<Void> updateCart(
        @PathVariable UUID cartId,
        @PathVariable Integer variantId,
        @Valid @RequestBody CartItemUpdateDto body
    ) {
        cartService.updateCartItem(cartId, variantId, body.getQuantity());

        return ResponseEntity.ok().build();
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
        return cartService.getCartBySessionId(sessionId)
            .orElseThrow(() -> new NotFoundException("Cart with session ID: " + sessionId + " not found"));
    }

    @Operation(
        summary = "Get cart for current user (annonymouse or logged-in)"
    )
    @GetMapping("/my-cart")
    public ResponseEntity<CartDto> getMyCart(
        @CookieValue(name = CART_COOKIE_NAME, required = false) String sessionId
    ) {
        return ResponseEntity.ok(cartService.getCart(sessionId));
    }

    @Operation(
        summary = "Associate anonymous cart with logged-in user (after login)"
    )
    @PostMapping("/associate")
    public void associateCart(
        @CookieValue(name = CART_COOKIE_NAME, required = false) String sessionId,
        Authentication authentication,
        HttpServletResponse response
    ) {
        Integer userId = (Integer) authentication.getPrincipal();
        cartService.associateCartWithCustomer(userId, sessionId);
        response.addCookie(getCookie("", 0));
    }

    private Cookie getCookie(String value, int maxAge) {
        return cookieService.getCookie(
            CART_COOKIE_NAME,
            value,
            CART_COOKIE_PATH,
            maxAge
        );
    }

    private Cookie getCookie(String value) {
        return getCookie(value, 3000);
    }
}
