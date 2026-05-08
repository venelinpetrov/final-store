package com.vpe.finalstore.discount.services;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.discount.dtos.CouponCreateDto;
import com.vpe.finalstore.discount.dtos.CouponDto;
import com.vpe.finalstore.discount.entities.Coupon;
import com.vpe.finalstore.discount.mappers.CouponMapper;
import com.vpe.finalstore.discount.repositories.CouponRepository;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import com.vpe.finalstore.exceptions.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CouponService {

	private final CouponRepository couponRepository;
	private final DiscountRepository discountRepository;
	private final CouponMapper couponMapper;

	@Transactional
	public CouponDto createSharedCoupon(CouponCreateDto dto) {
		var discount = discountRepository.findById(dto.getDiscountId())
			.orElseThrow(() -> new NotFoundException("Discount not found"));

		var coupon = new Coupon();
		coupon.setCode(dto.getCode());
		coupon.setDiscount(discount);
		coupon.setUsageLimit(dto.getUsageLimit());

		coupon = couponRepository.save(coupon);

		return couponMapper.toDto(coupon);
	}

}
