package com.vpe.finalstore.discount.services;

import com.vpe.finalstore.discount.dtos.AppliedDiscountDto;
import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.dtos.DiscountDto;
import com.vpe.finalstore.discount.enums.DiscountConditionType;
import com.vpe.finalstore.discount.mappers.DiscountMapper;
import com.vpe.finalstore.discount.repositories.AppliedDiscountRepository;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import com.vpe.finalstore.product.exceptions.VariantNotFoundException;
import com.vpe.finalstore.product.repositories.ProductVariantRepository;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final EntityManager entityManager;
    private final ProductVariantRepository variantRepository;
    private final AppliedDiscountRepository appliedDiscountRepository;

    public Page<DiscountDto> getDiscounts(
        Boolean isActive,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        Pageable pageable
    ) {
        return discountRepository
            .getDiscounts(isActive, validFrom, validUntil, pageable)
            .map(discountMapper::toDto);
    }

    public Page<AppliedDiscountDto> getAppliedDiscounts(Pageable pageable) {
        return appliedDiscountRepository.findAll(pageable).map(discountMapper::toDto);
    }

    @Transactional
    public DiscountDto createDiscount(DiscountCreateDto discountCreateDto) {
        if (discountCreateDto.getDiscountConditions() != null) {
            discountCreateDto.getDiscountConditions().stream()
                .filter(condition -> condition.getConditionType() == DiscountConditionType.VARIANT)
                .forEach(condition -> {
                    Integer variantId = condition.getIntValue();
                    if (!variantRepository.existsById(variantId)) {
                        throw new VariantNotFoundException();
                    }
                });
        }

        var discountEntity = discountMapper.toEntity(discountCreateDto);
        discountEntity = discountRepository.save(discountEntity);

        entityManager.refresh(discountEntity);

        return discountMapper.toDto(discountEntity);
    }

}
