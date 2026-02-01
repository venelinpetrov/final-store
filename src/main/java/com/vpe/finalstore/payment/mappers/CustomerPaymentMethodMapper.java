package com.vpe.finalstore.payment.mappers;

import com.vpe.finalstore.payment.dtos.CustomerPaymentMethodDto;
import com.vpe.finalstore.payment.entities.CustomerPaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerPaymentMethodMapper {
    public CustomerPaymentMethodDto toDto(CustomerPaymentMethod entity) {
        if (entity == null) {
            return null;
        }

        var dto = new CustomerPaymentMethodDto();
        dto.setMethodId(entity.getMethodId());
        dto.setCustomerId(entity.getCustomer().getCustomerId());
        dto.setStripeMethodId(entity.getStripeMethodId());
        dto.setMethodType(entity.getMethodType());
        dto.setCardBrand(entity.getCardBrand());
        dto.setLast4(entity.getLast4());
        dto.setExpMonth(entity.getExpMonth());
        dto.setExpYear(entity.getExpYear());
        dto.setIsDefault(entity.getIsDefault());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public List<CustomerPaymentMethodDto> toDto(List<CustomerPaymentMethod> entities) {
        return entities.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}

