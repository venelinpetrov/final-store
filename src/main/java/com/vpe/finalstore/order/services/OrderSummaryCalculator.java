package com.vpe.finalstore.order.services;

import com.vpe.finalstore.discount.entities.Coupon;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.price.services.PricingService;
import com.vpe.finalstore.tax.services.TaxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderSummaryCalculator {
    private final TaxService taxService;
    private final PricingService pricingService;

    public void calculateOrderSummary(Order order) {
        calculateOrderSummary(order, null);
    }

    public void calculateOrderSummary(Order order, Coupon coupon) {
        var pricing = pricingService.calculateOrder(order, coupon);
        var taxResult = pricing.taxResult();

        order.setSubtotal(pricing.subtotal());
        order.setTax(taxResult.getTaxAmount());
        order.setStripeTaxCalculationId(taxResult.getStripeTaxCalculationId());
        order.setTaxBreakdown(taxService.serializeTaxBreakdown(taxResult.getBreakdown()));
        order.setDiscountAmount(pricing.discountAmount());
        order.setShippingCost(pricing.shippingCost());
        order.setTotal(pricing.total());
    }
}

