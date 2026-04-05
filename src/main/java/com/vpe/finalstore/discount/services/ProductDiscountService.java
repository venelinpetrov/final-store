package com.vpe.finalstore.discount.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.discount.dtos.ProductDiscountCreateDto;
import com.vpe.finalstore.discount.entities.ProductDiscount;
import com.vpe.finalstore.discount.repositories.ProductDiscountRepository;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductDiscountService {
	private final ProductVariantRepository productVariantRepository;
	private final ProductDiscountRepository productDiscountRepository;

	@Transactional
	public ProductDiscount createDiscount(ProductDiscountCreateDto dto) {
		var existingActiveDiscount = productDiscountRepository.findActiveDiscount(dto.getVariantId(), LocalDateTime.now());

		if (existingActiveDiscount.isPresent()) {
			throw new IllegalStateException("Variant already has an active discount");
		}

		var discount = new ProductDiscount();

		var variant = productVariantRepository.findById(dto.getVariantId())
			.orElseThrow(VariantNotFoundException::new);

		discount.setProductVariant(variant);
		discount.setDiscountPercentage(dto.getDiscountPercentage());
		discount.setValidFrom(dto.getValidFrom());
		discount.setValidUntil(dto.getValidUntil());

		discount = productDiscountRepository.save(discount);

		return discount;
	}
}
