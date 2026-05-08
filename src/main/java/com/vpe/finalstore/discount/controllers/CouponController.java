package com.vpe.finalstore.discount.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.vpe.finalstore.discount.dtos.CouponCreateDto;
import com.vpe.finalstore.discount.dtos.CouponDto;
import com.vpe.finalstore.discount.services.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@RequestMapping("/api/coupons")
public class CouponController {

	private final CouponService couponService;

	@Operation(
		summary = "Create shared coupon"
	)
	@PostMapping
	public ResponseEntity<CouponDto> postMethodName(@Valid @RequestBody CouponCreateDto body, UriComponentsBuilder uriBuilder) {

		var couponDto = couponService.createSharedCoupon(body);
		var uri = uriBuilder
			.path("/api/coupons/{id}")
			.buildAndExpand(couponDto.getCouponId())
			.toUri();

		return ResponseEntity.created(uri).body(couponDto);
	}

}
