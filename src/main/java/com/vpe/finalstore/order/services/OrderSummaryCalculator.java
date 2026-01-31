package com.vpe.finalstore.order.services;

import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating order financial summaries (subtotal, tax, shipping, total).
 */
@Service
public class OrderSummaryCalculator {

    // Configuration constants - these could be moved to database at later stage
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax rate
    private static final BigDecimal SHIPPING_COST = new BigDecimal("5.00"); // $5.00 flat shipping

    /**
     * Calculate and set all financial summary fields for an order.
     */
    public void calculateOrderSummary(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal tax = calculateTax(subtotal);
        BigDecimal shippingCost = calculateShippingCost(order);
        BigDecimal total = calculateTotal(subtotal, tax, shippingCost);

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
    }

    private BigDecimal calculateSubtotal(Order order) {
        return order.getOrderItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateItemTotal(OrderItem item) {
        return item.getUnitPrice()
            .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal
            .multiply(TAX_RATE)
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate shipping cost for an order.
     * Currently uses a flat rate, but could be enhanced to:
     * - Calculate based on weight/dimensions
     * - Vary by shipping address (domestic vs international)
     * - Offer free shipping for orders above a threshold
     */
    private BigDecimal calculateShippingCost(Order order) {
        // Simple flat rate for now
        return SHIPPING_COST;
    }

    private BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal shippingCost) {
        return subtotal
            .add(tax)
            .add(shippingCost)
            .setScale(2, RoundingMode.HALF_UP);
    }
}

