package com.vpe.finalstore.discount.services;

import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.dtos.DiscountDto;
import com.vpe.finalstore.discount.mappers.DiscountMapper;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final EntityManager entityManager;

    @Transactional
    public DiscountDto createDiscount(DiscountCreateDto discountCreateDto) {
        var discountEntity = discountMapper.toEntity(discountCreateDto);
        discountEntity = discountRepository.save(discountEntity);

        entityManager.refresh(discountEntity);

        return discountMapper.toDto(discountEntity);
    }

}
