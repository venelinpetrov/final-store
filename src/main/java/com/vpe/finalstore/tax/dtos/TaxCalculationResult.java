package com.vpe.finalstore.tax.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Result of a tax calculation operation
 * Contains both the total tax amount and detailed breakdown
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxCalculationResult {
    /**
     * Total tax amount (sum of all tax components)
     */
    private BigDecimal taxAmount;

    /**
     * Stripe Tax Calculation ID (null if using fallback)
     */
    private String stripeTaxCalculationId;

    /**
     * Detailed tax breakdown (null if using fallback)
     */
    private TaxBreakdownDto breakdown;
}
