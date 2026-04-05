package com.vpe.finalstore.discount.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.vpe.finalstore.discount.dtos.ProductDiscountCreateDto;
import com.vpe.finalstore.discount.dtos.ProductDiscountDto;
import com.vpe.finalstore.discount.mappers.ProductDiscountMapper;
import com.vpe.finalstore.discount.services.ProductDiscountService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/api/product-discounts")
class ProductDiscountController {

	private final ProductDiscountService productDiscountService;
	private final ProductDiscountMapper productDiscountMapper;

	@PostMapping
	public ResponseEntity<ProductDiscountDto> createDiscount(
		@Valid @RequestBody ProductDiscountCreateDto body,
		UriComponentsBuilder uriBuilder
	) {
		var discount = productDiscountService.createDiscount(body);

		var uri = uriBuilder
			.path("/api/product-discounts/{discountId}")
			.buildAndExpand(discount.getDiscountId())
			.toUri();

		var dto = productDiscountMapper.toDto(discount);
		return ResponseEntity.created(uri).body(dto);
	}

}
