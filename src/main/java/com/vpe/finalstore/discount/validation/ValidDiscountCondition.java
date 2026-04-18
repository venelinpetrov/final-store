package com.vpe.finalstore.discount.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiscountConditionValidator.class)
public @interface ValidDiscountCondition {
    String message() default "Invalid discount condition configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
