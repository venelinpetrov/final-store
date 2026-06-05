package com.vpe.finalstore.tax.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a single tax component (e.g., state tax, local tax, VAT)
 * from a specific jurisdiction
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxComponentDto {
    /**
     * Type of tax (e.g., "state_sales_tax", "local_sales_tax", "vat", "gst")
     */
    private String type;

    /**
     * Jurisdiction name (e.g., "California", "Los Angeles", "United Kingdom")
     */
    private String jurisdiction;

    /**
     * Tax rate as decimal (e.g., 0.0725 for 7.25%)
     */
    private BigDecimal rate;

    /**
     * Amount this tax applies to (taxable amount)
     */
    private BigDecimal taxableAmount;

    /**
     * Calculated tax amount for this component
     */
    private BigDecimal amount;
}
