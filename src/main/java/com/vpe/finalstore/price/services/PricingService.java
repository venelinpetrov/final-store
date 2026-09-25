package com.vpe.finalstore.price.services;

import com.vpe.finalstore.auth.config.AuthService;
import com.vpe.finalstore.cart.entities.Cart;
import com.vpe.finalstore.cart.entities.CartItem;
import com.vpe.finalstore.customer.entities.CustomerAddress;
import com.vpe.finalstore.discount.entities.Coupon;
import com.vpe.finalstore.discount.services.DiscountService;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderItem;
import com.vpe.finalstore.payment.config.PaymentConfig;
import com.vpe.finalstore.price.dtos.PriceSummary;
import com.vpe.finalstore.tax.dtos.TaxCalculationResult;
import com.vpe.finalstore.tax.services.TaxService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@AllArgsConstructor
public class PricingService {

    private final DiscountService discountService;
    private final TaxService taxService;
    private final PaymentConfig paymentConfig;
    private final AuthService authService;

    // TODO remove hardcoding costs and implement them
    private static final BigDecimal SHIPPING_COST = new BigDecimal("5.00"); // $5.00 flat shipping

    public PriceSummary calculateCart(Cart cart, Coupon coupon) {
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal discount = discountService.calculateDiscount(cart, subtotal, coupon);

        TaxCalculationResult taxResult = calculateEstimatedTax(cart);
        BigDecimal tax = taxResult == null ? null: taxResult.getTaxAmount();

        BigDecimal total = calculateTotal(subtotal, tax, discount, BigDecimal.ZERO);

        return new PriceSummary(
            subtotal,
            discount,
            null,
            taxResult,
            total
        );
    }

    public PriceSummary calculateOrder(Order order, Coupon coupon) {
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal shippingCost = calculateShippingCost(order);

        TaxCalculationResult taxResult = calculateTax(order, shippingCost);
        BigDecimal tax = taxResult.getTaxAmount();

        BigDecimal discount = discountService.calculateDiscount(order, subtotal, coupon);
        BigDecimal total = calculateTotal(subtotal, tax, discount, shippingCost);

        return new PriceSummary(
            subtotal,
            discount,
            shippingCost,
            taxResult,
            total
        );
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getCartItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateItemTotal(CartItem item) {
        return item.getVariant().getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
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

    private TaxCalculationResult calculateTax(Order order, BigDecimal shippingCost) {
        // Convert shipping cost to cents for Stripe
        Long shippingCostInCents = shippingCost != null
            ? shippingCost.multiply(new BigDecimal("100")).longValue()
            : null;

        return taxService.calculateTax(
            order,
            paymentConfig.getCurrency(),
            shippingCostInCents
        );
    }

    private TaxCalculationResult calculateEstimatedTax(Cart cart) {
        var user = authService.getCurrentUser();
        CustomerAddress customerAddress = null;
        if (user != null) {
            customerAddress = user.getCustomer().getAddresses().stream().filter(address -> {
                // TODO: add address type enum
                if (address.getAddressType().getAddressTypeId() == 4) {
                    return true;
                }
                return false;
            })
            .findFirst()
            .orElseGet(() -> user.getCustomer().getAddresses().stream().findFirst().orElse(null));
        }

        if (customerAddress == null) {
            return null;
        }

        return taxService.calculateEstimatedTax(
            cart,
            paymentConfig.getCurrency(),
            customerAddress,
            null
        );
    }

    private BigDecimal calculateShippingCost(Order cart) {
        // TODO: implement shipping cost calculation
        // Simple flat rate for now
        return SHIPPING_COST;
    }

    private BigDecimal calculateTotal(
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal shippingCost
    ) {
        return subtotal
            .add(tax)
            .add(shippingCost == null ? BigDecimal.ZERO : shippingCost)
            .subtract(discount)
            .setScale(2, RoundingMode.HALF_UP);
    }
}
