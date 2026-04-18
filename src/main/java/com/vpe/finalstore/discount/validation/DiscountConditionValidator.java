package com.vpe.finalstore.discount.validation;

import com.vpe.finalstore.discount.dtos.DiscountConditionCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscountConditionValidator implements ConstraintValidator<ValidDiscountCondition, DiscountConditionCreateDto> {

    @Override
    public boolean isValid(DiscountConditionCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getConditionType() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        switch (dto.getConditionType()) {
            case MIN_QUANTITY:
                // MIN_QUANTITY should use intValue
                if (dto.getIntValue() == null) {
                    context.buildConstraintViolationWithTemplate("intValue is required for MIN_QUANTITY condition")
                        .addPropertyNode("intValue")
                        .addConstraintViolation();
                    isValid = false;
                }
                if (dto.getDecimalValue() != null || dto.getStringValue() != null) {
                    context.buildConstraintViolationWithTemplate("Only intValue should be set for MIN_QUANTITY condition")
                        .addPropertyNode("conditionType")
                        .addConstraintViolation();
                    isValid = false;
                }
                break;

            case CUSTOMER_GROUP:
                // CUSTOMER_GROUP should use stringValue
                if (dto.getStringValue() == null || dto.getStringValue().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("stringValue is required for CUSTOMER_GROUP condition")
                        .addPropertyNode("stringValue")
                        .addConstraintViolation();
                    isValid = false;
                }
                if (dto.getDecimalValue() != null || dto.getIntValue() != null) {
                    context.buildConstraintViolationWithTemplate("Only stringValue should be set for CUSTOMER_GROUP condition")
                        .addPropertyNode("conditionType")
                        .addConstraintViolation();
                    isValid = false;
                }
                break;

            case VARIANT:
                // VARIANT should use intValue (variant ID)
                if (dto.getIntValue() == null) {
                    context.buildConstraintViolationWithTemplate("intValue is required for VARIANT condition")
                        .addPropertyNode("intValue")
                        .addConstraintViolation();
                    isValid = false;
                }
                if (dto.getDecimalValue() != null || dto.getStringValue() != null) {
                    context.buildConstraintViolationWithTemplate("Only intValue should be set for VARIANT condition")
                        .addPropertyNode("conditionType")
                        .addConstraintViolation();
                    isValid = false;
                }
                break;

            default:
                // Unknown condition type
                context.buildConstraintViolationWithTemplate("Unknown condition type")
                    .addPropertyNode("conditionType")
                    .addConstraintViolation();
                isValid = false;
        }

        return isValid;
    }
}
