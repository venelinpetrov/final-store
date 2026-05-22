package com.vpe.finalstore.discount.controllers;

import com.vpe.finalstore.cart.dtos.CartDto;
import com.vpe.finalstore.discount.dtos.ApplyCouponDto;
import com.vpe.finalstore.discount.dtos.CouponCreateDto;
import com.vpe.finalstore.discount.dtos.CouponDto;
import com.vpe.finalstore.discount.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Create a new coupon")
    @PostMapping("/coupons")
    public ResponseEntity<CouponDto> createCoupon(@Valid @RequestBody CouponCreateDto body) {
        var couponDto = couponService.createCoupon(body);

        return ResponseEntity
            .created(URI.create("/api/coupons/" + couponDto.getCouponId()))
            .body(couponDto);
    }

    @Operation(summary = "Apply a coupon to a cart", description = "Apply a coupon code to the cart. Only the cart owner can apply coupons.")
    @PreAuthorize("@cartSecurity.isOwner(#cartId, authentication)")
    @PostMapping("/carts/{cartId}/coupon")
    public ResponseEntity<CartDto> applyCoupon(
        @PathVariable UUID cartId,
        @Valid @RequestBody ApplyCouponDto body
    ) {
        var cartDto = couponService.applyCouponToCart(cartId, body.getCode());

        return ResponseEntity.ok(cartDto);
    }

    @Operation(summary = "Remove coupon from a cart", description = "Remove the applied coupon from the cart. Only the cart owner can remove coupons.")
    @PreAuthorize("@cartSecurity.isOwner(#cartId, authentication)")
    @DeleteMapping("/carts/{cartId}/coupon")
    public ResponseEntity<CartDto> removeCoupon(@PathVariable UUID cartId) {
        var cartDto = couponService.removeCouponFromCart(cartId);

        return ResponseEntity.ok(cartDto);
    }

    @Operation(summary = "Validate a coupon code", description = "Check if a coupon code is valid and can be used")
    @GetMapping("/coupons/validate/{code}")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        boolean isValid = couponService.isValidCoupon(code);

        return ResponseEntity.ok(isValid);
    }
}
