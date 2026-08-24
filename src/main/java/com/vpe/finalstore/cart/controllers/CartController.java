package com.vpe.finalstore.cart.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.cart.dtos.CartItemAddDto;
import com.vpe.finalstore.cart.dtos.CartItemUpdateDto;
import com.vpe.finalstore.cart.exceptions.CartNotFoundException;
import com.vpe.finalstore.cart.services.CartService;
import com.vpe.finalstore.common.services.CookieService;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.users.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    private final UserRepository userRepository;
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
    ResponseEntity<CartDto> createCart(
        @CookieValue(name = CART_COOKIE_NAME, required = false) String sessionId,
        HttpServletResponse response,
        UriComponentsBuilder uriBuilder
    ) {
        CartDto cartDto = null;

        if (sessionId == null) {
            cartDto = cartService.createCart();
            sessionId = cartDto.getSessionId().toString();
            response.addCookie(getCookie(sessionId));
            var uri = uriBuilder.path("api/carts/{cart_id}")
                .buildAndExpand(cartDto.getCartId())
                .toUri();

            return ResponseEntity.created(uri)
                .body(cartDto);
        } else {
            cartDto = cartService.getCartBySessionId(UUID.fromString(sessionId))
                .orElseThrow(CartNotFoundException::new);

            return ResponseEntity.ok(cartDto);
        }
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
        summary = "Get cart for current logged-in user"
    )
    @GetMapping("/my-cart")
    public CartDto getMyCart(Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return cartService.getCartByCustomerId(user.getCustomer().getCustomerId())
            .orElseThrow(() -> new NotFoundException("Cart not found for customer"));
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

        return cartService.associateCartWithCustomer(sessionId, user.getCustomer().getCustomerId());
    }

    private Cookie getCookie(String value) {
        return cookieService.getCookie(
            CART_COOKIE_NAME,
            value,
            CART_COOKIE_PATH,
            3000);
    }
}
