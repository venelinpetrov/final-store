package com.vpe.finalstore.price.dtos;

import java.math.BigDecimal;

import com.vpe.finalstore.tax.dtos.TaxCalculationResult;

public record PriceSummary (
    BigDecimal subtotal,
    BigDecimal discountAmount,
    BigDecimal shippingCost,
    TaxCalculationResult taxResult,
    BigDecimal total
) { }
