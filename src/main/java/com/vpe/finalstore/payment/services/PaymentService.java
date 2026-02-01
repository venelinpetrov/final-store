package com.vpe.finalstore.payment.services;

import com.stripe.model.PaymentIntent;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.payment.config.PaymentConfig;
import com.vpe.finalstore.payment.entities.Invoice;
import com.vpe.finalstore.payment.entities.Payment;
import com.vpe.finalstore.payment.entities.PaymentMethod;
import com.vpe.finalstore.payment.entities.PaymentStatus;
import com.vpe.finalstore.payment.enums.PaymentStatusType;
import com.vpe.finalstore.payment.repositories.PaymentMethodRepository;
import com.vpe.finalstore.payment.repositories.PaymentRepository;
import com.vpe.finalstore.payment.repositories.PaymentStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {
    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PaymentConfig paymentConfig;

    @Transactional
    public Map<String, String> createPaymentIntentForCart(UUID cartId, Integer customerId) {
        var cart = cartRepository.getCartWithItems(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cannot create payment for empty cart");
        }

        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        BigDecimal total = cart.calculateTotal();

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Cart total must be greater than zero");
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("customerId", customerId.toString());
        metadata.put("cartId", cartId.toString());
        metadata.put("customerEmail", customer.getUser() != null ? customer.getUser().getEmail() : "");

        PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                total,
                paymentConfig.getCurrency(),
                null,  // Stripe customer ID (we'll add this later when implementing saved payment methods)
                null,  // Payment method ID (customer will provide via frontend)
                metadata
        );

        log.info("Created PaymentIntent {} for cart {} with amount ${}",
                paymentIntent.getId(), cartId, total);

        // Return client secret for frontend
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("amount", total.toString());

        return response;
    }

    public Payment getPaymentByStripeIntentId(String stripePaymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    /**
     * Retrieve payment by ID with all details
     *
     * @param paymentId Payment ID
     * @return Payment entity with details
     */
    public Payment getPaymentById(Integer paymentId) {
        return paymentRepository.findPaymentWithDetails(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    /**
     * Verify payment status with Stripe
     *
     * @param paymentIntentId Stripe PaymentIntent ID
     * @return Payment status from Stripe
     */
    public String verifyPaymentStatus(String paymentIntentId) {
        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);
        return paymentIntent.getStatus();
    }

    /**
     * Create payment record for an invoice
     * This is called when creating an order after successful payment
     *
     * @param paymentIntentId Stripe PaymentIntent ID
     * @param invoice The invoice to associate with this payment
     * @return Created Payment entity
     */
    @Transactional
    public Payment createPaymentForInvoice(String paymentIntentId, Invoice invoice) {
        // Retrieve PaymentIntent from Stripe to verify it succeeded
        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new BadRequestException("Payment has not succeeded yet. Status: " + paymentIntent.getStatus());
        }

        // Check if payment already exists for this PaymentIntent
        var existingPayment = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (existingPayment.isPresent()) {
            log.warn("Payment already exists for PaymentIntent {}", paymentIntentId);
            return existingPayment.get();
        }

        // Get payment status
        PaymentStatusType statusType = mapStripeStatus(paymentIntent.getStatus());
        PaymentStatus status = paymentStatusRepository.findByName(statusType)
                .orElseThrow(() -> new NotFoundException("Payment status not found: " + statusType));

        // Get payment method (default to "card" for now)
        PaymentMethod method = paymentMethodRepository.findByName("card")
                .orElse(null);

        // Create payment record
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setStripePaymentIntentId(paymentIntentId);
        payment.setStripeChargeId(paymentIntent.getLatestCharge());
        payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100))); // Convert cents to dollars
        payment.setStatus(status);
        payment.setMethod(method);

        // Extract Stripe customer ID from PaymentIntent if available
        if (paymentIntent.getCustomer() != null) {
            payment.setStripeCustomerId(paymentIntent.getCustomer());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created payment record {} for PaymentIntent {} and Invoice {}",
                savedPayment.getPaymentId(), paymentIntentId, invoice.getInvoiceId());

        return savedPayment;
    }

    /**
     * Map Stripe payment status to our PaymentStatusType
     *
     * @param stripeStatus Stripe payment status
     * @return Our PaymentStatusType
     */
    private PaymentStatusType mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatusType.SUCCEEDED;
            case "processing" -> PaymentStatusType.PROCESSING;
            case "requires_payment_method", "requires_confirmation", "requires_action" -> PaymentStatusType.PENDING;
            case "canceled" -> PaymentStatusType.CANCELLED;
            default -> PaymentStatusType.FAILED;
        };
    }
}

