package com.vpe.finalstore.tax.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a line item for Stripe Tax calculation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxLineItemDto {
    /**
     * Amount in cents (Stripe uses smallest currency unit)
     */
    private Long amountInCents;

    /**
     * Reference ID (e.g., variant ID, product ID)
     */
    private String reference;

    /**
     * Stripe tax code (e.g., "txcd_99999999" for general tangible goods)
     * See: https://stripe.com/docs/tax/tax-codes
     */
    private String taxCode;
}
