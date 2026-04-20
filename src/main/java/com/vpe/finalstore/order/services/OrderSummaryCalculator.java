package com.vpe.finalstore.order.services;

import com.vpe.finalstore.discount.entities.Discount;
import com.vpe.finalstore.discount.repositories.DiscountRepository;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrderSummaryCalculator {

    private final DiscountRepository discountRepository;

    // TODO remove hardcoding rates and costs and implement them
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax rate
    private static final BigDecimal SHIPPING_COST = new BigDecimal("5.00"); // $5.00 flat shipping

    public void calculateOrderSummary(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal tax = calculateTax(subtotal);
        BigDecimal discount = calculateDiscount(order);
        BigDecimal shippingCost = calculateShippingCost(order);
        BigDecimal total = calculateTotal(subtotal, tax, discount, shippingCost);

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setDiscountAmount(discount);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
    }

    public Map<OrderItem, Discount> getAppliedDiscounts(Order order) {
        Map<OrderItem, Discount> appliedDiscounts = new HashMap<>();

        for (OrderItem item : order.getOrderItems()) {
            if (item.getDiscountAmount() != null &&
                item.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {

                var discount = discountRepository.findActiveDiscountForVariant(
                    item.getVariant().getVariantId()
                );

                discount.ifPresent(d -> appliedDiscounts.put(item, d));
            }
        }

        return appliedDiscounts;
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

    private BigDecimal calculateDiscount(Order order) {
       return order.getOrderItems().stream()
            .map(this::calculateItemDiscount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemDiscount(OrderItem item) {
        var variant = item.getVariant();

        var existingDiscount = discountRepository.findActiveDiscountForVariant(variant.getVariantId());

        if (existingDiscount.isEmpty()) {
            item.setDiscountAmount(BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }

        var discount = existingDiscount.get();
        var unitPrice = variant.getUnitPrice();

        BigDecimal discountAmount = switch (discount.getDiscountType()) {
            case PERCENTAGE -> unitPrice
                .multiply(discount.getValue())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(item.getQuantity()));
            case FIXED -> {
                BigDecimal discountPerItem = discount.getValue().min(unitPrice);
                yield discountPerItem.multiply(BigDecimal.valueOf(item.getQuantity()));
            }
            case BUY_X_GET_Y -> BigDecimal.ZERO;
        };

        item.setDiscountAmount(discountAmount);
        return discountAmount;
    }

    private BigDecimal calculateShippingCost(Order order) {
        // TODO: implement shipping cost calculation
        // Simple flat rate for now
        return SHIPPING_COST;
    }

    private BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal discount, BigDecimal shippingCost) {
        return subtotal
            .add(tax)
            .add(shippingCost)
            .subtract(discount)
            .setScale(2, RoundingMode.HALF_UP);
    }
}

