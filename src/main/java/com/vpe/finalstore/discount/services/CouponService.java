package com.vpe.finalstore.discount.services;

import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.discount.dtos.CouponCreateDto;
import com.vpe.finalstore.discount.dtos.CouponDto;
import com.vpe.finalstore.discount.entities.Coupon;
import com.vpe.finalstore.discount.mappers.DiscountMapper;
import com.vpe.finalstore.discount.repositories.CouponRepository;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final DiscountRepository discountRepository;
    private final CartRepository cartRepository;
    private final DiscountMapper discountMapper;
    private final EntityManager entityManager;

    @Transactional
    public CouponDto createCoupon(CouponCreateDto dto) {
        // Validate discount exists
        var discount = discountRepository.findById(dto.getDiscountId())
            .orElseThrow(() -> new NotFoundException("Discount not found"));

        // Check if code already exists
        if (couponRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Coupon code already exists: " + dto.getCode());
        }

        var coupon = new Coupon();
        coupon.setCode(dto.getCode().toUpperCase());
        coupon.setDiscount(discount);
        coupon.setUsageLimit(dto.getUsageLimit());
        coupon.setTimesUsed(0);
        coupon.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        coupon = couponRepository.save(coupon);
        entityManager.refresh(coupon);

        return discountMapper.toDto(coupon);
    }

    @Transactional
    public Cart applyCouponToCart(UUID cartId, String code) {
        var cart = cartRepository.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found"));

        var coupon = couponRepository.findValidCouponByCode(code.toUpperCase())
            .orElseThrow(() -> new BadRequestException(
                "Invalid or expired coupon code: " + code
            ));

        // Validate coupon hasn't reached usage limit
        if (coupon.getUsageLimit() != null &&
            coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            throw new BadRequestException("Coupon has reached its usage limit");
        }

        cart.setCoupon(coupon);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeCouponFromCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found"));

        cart.setCoupon(null);
        return cartRepository.save(cart);
    }

    @Transactional
    public void incrementUsageCount(Integer couponId) {
        var coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new NotFoundException("Coupon not found"));

        coupon.setTimesUsed(coupon.getTimesUsed() + 1);
        couponRepository.save(coupon);
    }

    public boolean isValidCoupon(String code) {
        return couponRepository.findValidCouponByCode(code.toUpperCase()).isPresent();
    }

    public CouponDto getCouponByCode(String code) {
        var coupon = couponRepository.findValidCouponByCode(code.toUpperCase())
            .orElseThrow(() -> new BadRequestException(
                "Invalid or expired coupon code: " + code
            ));
        return discountMapper.toDto(coupon);
    }
}
