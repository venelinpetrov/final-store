package com.vpe.finalstore.tax.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.tax.Calculation;
import com.vpe.finalstore.customer.entities.CustomerAddress;
import com.vpe.finalstore.exceptions.ServiceUnavailableException;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.payment.services.StripeService;
import com.vpe.finalstore.tax.dtos.TaxBreakdownDto;
import com.vpe.finalstore.tax.dtos.TaxCalculationResult;
import com.vpe.finalstore.tax.dtos.TaxComponentDto;
import com.vpe.finalstore.tax.dtos.TaxLineItemDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TaxService {
    private final StripeService stripeService;
    private final ObjectMapper objectMapper;

    public TaxCalculationResult calculateTax(
        Order order,
        CustomerAddress address,
        String currency,
        Long shippingCostInCents
    ) {
        try {
            List<TaxLineItemDto> lineItems = order.getOrderItems().stream().map(item -> {
                var amountInCents = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .multiply(new BigDecimal("100"))
                    .longValue();

                String taxCode = item.getVariant().getProduct().getTaxCode();
                if (taxCode == null || taxCode.isEmpty()) {
                    taxCode = "txcd_99999999"; // Default: General tangible goods
                }

                return new TaxLineItemDto(
                    amountInCents,
                    item.getVariant().getVariantId().toString(),
                    taxCode
                );
            }).toList();

            var calculation = stripeService.calculateTax(
                currency,
                lineItems,
                shippingCostInCents,
                address.getCountryCode(), // By "country" Stripe means "country code" (e.g., "US", "CA")
                address.getPostalCode(),
                address.getState(),
                address.getCity(),
                address.getStreet()
            );

            // Convert Stripe response to our DTOs
            var taxAmount = BigDecimal.valueOf(calculation.getTaxAmountExclusive())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            var breakdown = convertStripeBreakdown(calculation);

            log.info("Tax calculated via Stripe Tax API. Calculation ID: {}, Amount: {}", calculation.getId(), taxAmount);

            return new TaxCalculationResult(taxAmount, calculation.getId(), breakdown);

        } catch (StripeException e) {
            log.error("Stripe Tax API failed: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("Tax calculation service is currently unavailable. Please try again later.", e);
        } catch (Exception e) {
            log.error("Unexpected error during tax calculation: {}", e.getMessage(), e);
            throw new ServiceUnavailableException("Tax calculation service is currently unavailable. Please try again later.", e);
        }
    }

    private TaxBreakdownDto convertStripeBreakdown(Calculation calculation) {
        var total = BigDecimal.valueOf(calculation.getTaxAmountExclusive())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Extract tax breakdown from Stripe response
        var components = new ArrayList<TaxComponentDto>();

        if (calculation.getTaxBreakdown() != null && !calculation.getTaxBreakdown().isEmpty()) {
            for (var breakdown : calculation.getTaxBreakdown()) {
                var taxRateDetails = breakdown.getTaxRateDetails();
                if (taxRateDetails != null) {
                    var component = new TaxComponentDto();

                    // Type (e.g., "sales_tax", "vat", "gst")
                    component.setType(taxRateDetails.getTaxType() != null ? taxRateDetails.getTaxType() : "tax");

                    // Jurisdiction is build from country and state
                    var jurisdiction = new StringBuilder();
                    if (taxRateDetails.getCountry() != null) {
                        jurisdiction.append(taxRateDetails.getCountry());
                    }

                    if (taxRateDetails.getState() != null) {
                        if (jurisdiction.length() > 0) {
                            jurisdiction.append(" - ");
                        }
                        jurisdiction.append(taxRateDetails.getState());
                    }

                    component.setJurisdiction(jurisdiction.length() > 0 ? jurisdiction.toString() : "Unknown");

                    // Rate as decimal (Stripe returns percentage as string, e.g., "7.25" for 7.25%)
                    // Convert to decimal: 7.25 -> 0.0725
                    var rateDecimal = BigDecimal.ZERO;
                    if (taxRateDetails.getPercentageDecimal() != null) {
                        try {
                            rateDecimal = new BigDecimal(taxRateDetails.getPercentageDecimal())
                                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                        } catch (NumberFormatException e) {
                            log.warn("Failed to parse tax rate: {}", taxRateDetails.getPercentageDecimal());
                        }
                    }
                    component.setRate(rateDecimal);

                    // Tax amount for this component (convert from cents)
                    var amount = BigDecimal.valueOf(breakdown.getAmount())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    component.setAmount(amount);

                    // Taxable amount (convert from cents)
                    var taxableAmount = breakdown.getTaxableAmount() != null
                        ? BigDecimal.valueOf(breakdown.getTaxableAmount())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                    component.setTaxableAmount(taxableAmount);

                    components.add(component);
                }
            }
        }

        return new TaxBreakdownDto(total, components, calculation.getId());
    }

    public String serializeTaxBreakdown(TaxBreakdownDto breakdown) {
        try {
            return objectMapper.writeValueAsString(breakdown);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tax breakdown: {}", e.getMessage(), e);
            return null;
        }
    }
}
