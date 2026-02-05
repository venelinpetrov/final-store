package com.vpe.finalstore.payment.services;

import com.stripe.model.PaymentIntent;
import com.vpe.finalstore.cart.repositories.CartRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.services.OrderService;
import com.vpe.finalstore.payment.config.PaymentConfig;
import com.vpe.finalstore.payment.dtos.PaymentIntentResponseDto;
import com.vpe.finalstore.payment.entities.Invoice;
import com.vpe.finalstore.payment.entities.Payment;
import com.vpe.finalstore.payment.entities.PaymentMethod;
import com.vpe.finalstore.payment.entities.PaymentStatus;
import com.vpe.finalstore.payment.enums.PaymentStatusType;
import com.vpe.finalstore.payment.repositories.InvoiceRepository;
import com.vpe.finalstore.payment.repositories.PaymentMethodRepository;
import com.vpe.finalstore.payment.repositories.PaymentRepository;
import com.vpe.finalstore.payment.repositories.PaymentStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final OrderService orderService;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public PaymentIntentResponseDto createPaymentIntentForCart(UUID cartId, @NonNull Integer customerId, @NonNull Integer addressId) {
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
        metadata.put("addressId", addressId.toString());
        metadata.put("customerEmail", customer.getUser() != null ? customer.getUser().getEmail() : "");

        PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                total,
                paymentConfig.getCurrency(),
                null,  // Stripe customer ID (we'll add this later when implementing saved payment methods)
                null,  // Payment method ID (customer will provide via frontend)
                metadata
        );

        log.info("Created PaymentIntent {} for cart {} with amount {} {}",
                paymentIntent.getId(), cartId, total, paymentConfig.getCurrency());

        return new PaymentIntentResponseDto(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                total,
                paymentConfig.getCurrency()
        );
    }

    public Payment getPaymentByStripeIntentId(String stripePaymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    public Payment getPaymentById(Integer paymentId) {
        return paymentRepository.findPaymentWithDetails(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    public String verifyPaymentStatus(String paymentIntentId) {
        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);
        return paymentIntent.getStatus();
    }

    @Transactional
    public Payment createPaymentForInvoice(String paymentIntentId, Invoice invoice) {
        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

        if (!"succeeded".equals(paymentIntent.getStatus())) {
            throw new BadRequestException("Payment has not succeeded yet. Status: " + paymentIntent.getStatus());
        }

        var existingPayment = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (existingPayment.isPresent()) {
            log.warn("Payment already exists for PaymentIntent {}", paymentIntentId);
            return existingPayment.get();
        }

        PaymentStatusType statusType = mapStripeStatus(paymentIntent.getStatus());
        PaymentStatus status = paymentStatusRepository.findByName(statusType)
                .orElseThrow(() -> new NotFoundException("Payment status not found: " + statusType));

        PaymentMethod method = paymentMethodRepository.findByName("card")
                .orElse(null);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setStripePaymentIntentId(paymentIntentId);
        payment.setStripeChargeId(paymentIntent.getLatestCharge());
        payment.setAmount(BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100))); // Convert cents to dollars
        payment.setStatus(status);
        payment.setMethod(method);

        if (paymentIntent.getCustomer() != null) {
            payment.setStripeCustomerId(paymentIntent.getCustomer());
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created payment record {} for PaymentIntent {} and Invoice {}",
            savedPayment.getPaymentId(), paymentIntentId, invoice.getInvoiceId());

        return savedPayment;
    }

    private Invoice createInvoiceForOrder(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setCustomer(order.getCustomer());
        invoice.setInvoiceTotal(order.getTotal());
        invoice.setTax(order.getTax());
        invoice.setDiscount(BigDecimal.ZERO);
        invoice.setPaymentTotal(BigDecimal.ZERO);
        invoice.setDueDate(LocalDateTime.now().plusDays(30));

        return invoiceRepository.save(invoice);
    }

    private PaymentStatusType mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatusType.SUCCEEDED;
            case "processing" -> PaymentStatusType.PROCESSING;
            case "requires_payment_method", "requires_confirmation", "requires_action" -> PaymentStatusType.PENDING;
            case "canceled" -> PaymentStatusType.CANCELLED;
            default -> PaymentStatusType.FAILED;
        };
    }

    // ==================== Striep Webhook Handlers ====================

    @Transactional
    public void handlePaymentSucceeded(String paymentIntentId, UUID cartId, Integer customerId, Integer addressId) {
        log.info("Processing successful payment webhook for PaymentIntent: {}", paymentIntentId);

        // Check if we already processed this payment
        var existingPayment = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (existingPayment.isPresent()) {
            log.warn("Payment already processed for PaymentIntent {}, skipping", paymentIntentId);
            return;
        }

        Order order = orderService.createOrderFromCart(cartId, customerId, addressId);
        log.info("Created order {} from cart {} via webhook", order.getOrderId(), cartId);

        Invoice invoice = createInvoiceForOrder(order);
        log.info("Created invoice {} for order {} via webhook", invoice.getInvoiceId(), order.getOrderId());

        Payment payment = createPaymentForInvoice(paymentIntentId, invoice);
        log.info("Created payment record {} for invoice {} via webhook", payment.getPaymentId(), invoice.getInvoiceId());

        // TODO: Optionally notify customer about successful payment
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        log.warn("Processing failed payment webhook for PaymentIntent: {}", paymentIntentId);

        // Check if payment record exists
        var existingPayment = paymentRepository.findByStripePaymentIntentId(paymentIntentId);

        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            PaymentStatus failedStatus = paymentStatusRepository.findByName(PaymentStatusType.FAILED)
                .orElseThrow(() -> new NotFoundException("Payment status FAILED not found"));
            payment.setStatus(failedStatus);
            paymentRepository.save(payment);
            log.info("Updated payment {} status to FAILED", payment.getPaymentId());
        } else {
            log.info("No payment record found for failed PaymentIntent {}", paymentIntentId);
        }

        // TODO: Optionally notify customer about failed payment
    }
}
