package com.vpe.finalstore.discount.validation;

import com.vpe.finalstore.discount.dtos.DiscountCreateDto;
import com.vpe.finalstore.discount.enums.DiscountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class DiscountValidator implements ConstraintValidator<ValidDiscount, DiscountCreateDto> {

    @Override
    public boolean isValid(DiscountCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        // Validate percentage discount value is between 0 and 100
        if (dto.getDiscountType() == DiscountType.PERCENTAGE) {
            if (dto.getValue() != null && (dto.getValue().compareTo(BigDecimal.ZERO) < 0 || dto.getValue().compareTo(new BigDecimal("100")) > 0)) {
                context.buildConstraintViolationWithTemplate("Percentage discount value must be between 0 and 100")
                    .addPropertyNode("value")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate fixed discount value is positive
        if (dto.getDiscountType() == DiscountType.FIXED) {
            if (dto.getValue() != null && dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                context.buildConstraintViolationWithTemplate("Fixed discount value must be greater than 0")
                    .addPropertyNode("value")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate date range: validFrom must be before validUntil
        if (dto.getValidFrom() != null && dto.getValidUntil() != null) {
            if (dto.getValidFrom().isAfter(dto.getValidUntil()) || dto.getValidFrom().isEqual(dto.getValidUntil())) {
                context.buildConstraintViolationWithTemplate("validFrom must be before validUntil")
                    .addPropertyNode("validFrom")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate maxDiscountAmount is not greater than minOrderAmount (if both are set)
        if (dto.getMaxDiscountAmount() != null && dto.getMinOrderAmount() != null) {
            if (dto.getMaxDiscountAmount().compareTo(dto.getMinOrderAmount()) > 0) {
                context.buildConstraintViolationWithTemplate("maxDiscountAmount should not exceed minOrderAmount")
                    .addPropertyNode("maxDiscountAmount")
                    .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
