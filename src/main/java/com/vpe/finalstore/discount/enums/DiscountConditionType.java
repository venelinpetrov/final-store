package com.vpe.finalstore.discount.enums;

public enum DiscountConditionType {
    MIN_QUANTITY,    // Minimum quantity required (uses int_value)
    CUSTOMER_GROUP,  // Specific customer group (uses string_value)
    VARIANT          // Specific product variant (uses int_value for variant_id)
}
