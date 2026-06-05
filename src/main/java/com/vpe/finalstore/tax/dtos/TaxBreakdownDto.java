package com.vpe.finalstore.tax.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the complete tax breakdown for an order
 * Matches the structure returned by Stripe Tax API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxBreakdownDto {
    /**
     * Total tax amount across all jurisdictions
     */
    private BigDecimal total;

    /**
     * List of individual tax components (state, local, etc.)
     */
    private List<TaxComponentDto> breakdown;

    /**
     * Stripe Tax Calculation ID for reference
     */
    private String stripeTaxCalculationId;
}
