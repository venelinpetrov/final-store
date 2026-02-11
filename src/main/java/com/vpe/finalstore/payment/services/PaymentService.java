package com.vpe.finalstore.payment.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.BadRequestException;
import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.enums.OrderStatusType;
import com.vpe.finalstore.order.repositories.OrderRepository;
import com.vpe.finalstore.order.repositories.OrderStatusRepository;
import com.vpe.finalstore.order.services.OrderService;
import com.vpe.finalstore.payment.config.PaymentConfig;
import com.vpe.finalstore.payment.dtos.PaymentIntentResponseDto;
import com.vpe.finalstore.payment.entities.CustomerPaymentMethod;
import com.vpe.finalstore.payment.entities.Invoice;
import com.vpe.finalstore.payment.entities.Payment;
import com.vpe.finalstore.payment.entities.PaymentMethod;
import com.vpe.finalstore.payment.entities.PaymentStatus;
import com.vpe.finalstore.payment.enums.PaymentStatusType;
import com.vpe.finalstore.payment.repositories.CustomerPaymentMethodRepository;
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
    private final CustomerPaymentMethodRepository customerPaymentMethodRepository;
    private final CustomerRepository customerRepository;
    private final PaymentConfig paymentConfig;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final InvoiceRepository invoiceRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create order from cart, invoice, initial payment record, and PaymentIntent
     * Flow: Order (PENDING) → Invoice → Payment (PENDING) → PaymentIntent
     *
     * @param cartId Cart ID
     * @param customerId Customer ID
     * @param addressId Shipping address ID
     * @return PaymentIntent response with clientSecret
     */
    @Transactional
    public PaymentIntentResponseDto createPaymentIntentForCart(UUID cartId, @NonNull Integer customerId, @NonNull Integer addressId) {
        // 1. Create order from cart (status: PENDING)
        Order order = orderService.createOrderFromCart(cartId, customerId, addressId);
        log.info("Created order {} from cart {} with status PENDING", order.getOrderId(), cartId);

        // 2. Create invoice for the order
        Invoice invoice = createInvoiceForOrder(order);
        log.info("Created invoice {} for order {}", invoice.getInvoiceId(), order.getOrderId());

        // 3. Create initial payment record (PENDING status, attempt 1)
        Payment payment = createInitialPaymentRecord(invoice);
        log.info("Created initial payment record {} for invoice {}", payment.getPaymentId(), invoice.getInvoiceId());

        // 4. Create PaymentIntent with payment metadata
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Map<String, String> metadata = new HashMap<>();
        metadata.put("orderId", order.getOrderId().toString());
        metadata.put("invoiceId", invoice.getInvoiceId().toString());
        metadata.put("paymentId", payment.getPaymentId().toString());
        metadata.put("customerId", customerId.toString());
        metadata.put("customerEmail", customer.getUser() != null ? customer.getUser().getEmail() : "");

        PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                order.getTotal(),
                paymentConfig.getCurrency(),
                null,  // Stripe customer ID (we'll add this later when implementing saved payment methods)
                null,  // Payment method ID (customer will provide via frontend)
                metadata
        );

        // 5. Link PaymentIntent to payment record
        payment.setStripePaymentIntentId(paymentIntent.getId());
        paymentRepository.save(payment);

        log.info("Created PaymentIntent {} for payment {} with amount {} {}",
                paymentIntent.getId(), payment.getPaymentId(), order.getTotal(), paymentConfig.getCurrency());

        return new PaymentIntentResponseDto(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                order.getTotal(),
                paymentConfig.getCurrency()
        );
    }

    /**
     * Create initial payment record with PENDING status
     */
    private Payment createInitialPaymentRecord(Invoice invoice) {
        var pendingStatus = paymentStatusRepository.findByName(PaymentStatusType.PENDING)
                .orElseThrow(() -> new NotFoundException("Payment status PENDING not found"));

        var payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getInvoiceTotal());
        payment.setStatus(pendingStatus);
        payment.setAttemptNumber(1);

        return paymentRepository.save(payment);
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

    // ==================== Stripe Webhook Handlers ====================

    @Transactional
    public void handlePaymentSucceeded(String paymentIntentId, Integer paymentId) {
        log.info("Processing successful payment webhook for PaymentIntent: {}", paymentIntentId);

        // Get payment record
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        // Check if already processed (idempotency)
        if (payment.getStatus().getName() == PaymentStatusType.SUCCEEDED) {
            log.warn("Payment {} already marked as SUCCEEDED, skipping", paymentId);
            return;
        }

        PaymentIntent paymentIntent = stripeService.retrievePaymentIntent(paymentIntentId);

        var succeededStatus = paymentStatusRepository.findByName(PaymentStatusType.SUCCEEDED)
                .orElseThrow(() -> new NotFoundException("Payment status SUCCEEDED not found"));
        payment.setStatus(succeededStatus);

        payment.setStripeChargeId(paymentIntent.getLatestCharge());

        if (paymentIntent.getCustomer() != null) {
            payment.setStripeCustomerId(paymentIntent.getCustomer());
        }

        // Retrieve and save payment method details
        if (paymentIntent.getPaymentMethod() != null) {
            String stripePaymentMethodId = paymentIntent.getPaymentMethod();

            try {
                var stripePaymentMethod = stripeService.retrievePaymentMethod(stripePaymentMethodId);

                var customer = payment.getInvoice().getCustomer();

                var existingPaymentMethod = customerPaymentMethodRepository
                        .findByStripeMethodId(stripePaymentMethod.getId());

                if (existingPaymentMethod.isEmpty()) {
                    var customerPaymentMethod = saveCustomerPaymentMethod(
                            customer.getCustomerId(),
                            stripePaymentMethod
                    );
                    payment.setCustomerPaymentMethod(customerPaymentMethod);

                    log.info("Saved new payment method: {} ending in {} for customer {}",
                            customerPaymentMethod.getCardBrand(),
                            customerPaymentMethod.getLast4(),
                            customer.getCustomerId());
                }

                // Map Stripe payment method type to our PaymentMethod entity (for reporting)
                var ourPaymentMethod = mapStripePaymentMethodToOurs(stripePaymentMethod);
                if (ourPaymentMethod != null) {
                    payment.setMethod(ourPaymentMethod);
                }

                Map<String, Object> metadata = new HashMap<>();

                if (paymentIntent.getMetadata() != null && !paymentIntent.getMetadata().isEmpty()) {
                    metadata.putAll(paymentIntent.getMetadata());
                }

                metadata.put("paymentMethodType", stripePaymentMethod.getType());

                // Serialize metadata to JSON
                if (!metadata.isEmpty()) {
                    payment.setMetadata(objectMapper.writeValueAsString(metadata));
                }

            } catch (JsonProcessingException e) {
                log.error("Failed to serialize payment metadata for PaymentIntent {}: {}",
                        paymentIntentId, e.getMessage());
            } catch (Exception e) {
                log.error("Failed to save payment method for PaymentIntent {}: {}",
                        paymentIntentId, e.getMessage());
            }
        }

        paymentRepository.save(payment);

        log.info("✅ Payment {} succeeded for invoice {} (attempt {}) - Charge: {}",
                payment.getPaymentId(), payment.getInvoice().getInvoiceId(),
                payment.getAttemptNumber(), payment.getStripeChargeId());

        var invoice = payment.getInvoice();

        // Business rule: We only support full payment, no partial payments
        // Therefore, payment_total will always equal invoice_total when paid
        invoice.setPaymentTotal(payment.getAmount());
        invoiceRepository.save(invoice);
        var order = invoice.getOrder();

        log.info("📦 Order {} is ready for fulfillment (Status: {})",
                order.getOrderId(), order.getStatus().getName());

        // TODO: Optionally notify customer about successful payment
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId, Integer paymentId, String failureCode, String failureMessage) {
        log.warn("Processing failed payment webhook for PaymentIntent: {} - Reason: {} ({})",
                paymentIntentId, failureMessage, failureCode);

        Payment currentPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        var failedStatus = paymentStatusRepository.findByName(PaymentStatusType.FAILED)
                .orElseThrow(() -> new NotFoundException("Payment status FAILED not found"));
        currentPayment.setStatus(failedStatus);
        currentPayment.setFailureCode(failureCode);
        currentPayment.setFailureReason(failureMessage);
        paymentRepository.save(currentPayment);

        log.info("❌ Payment {} failed (attempt {}) - Reason: {}",
                currentPayment.getPaymentId(), currentPayment.getAttemptNumber(), failureMessage);

        // Check how many failed attempts exist for this invoice
        Invoice invoice = currentPayment.getInvoice();
        long failedAttempts = paymentRepository.findByInvoice(invoice).stream()
                .filter(p -> p.getStatus().getName() == PaymentStatusType.FAILED)
                .count();

        final int MAX_PAYMENT_ATTEMPTS = 3;
        if (failedAttempts >= MAX_PAYMENT_ATTEMPTS) {
            // Cancel order after 3 failed attempts
            Order order = invoice.getOrder();
            var canceledStatus = orderStatusRepository.findByName(OrderStatusType.CANCELED)
                    .orElseThrow(() -> new NotFoundException("Order status CANCELED not found"));
            order.setStatus(canceledStatus);
            orderRepository.save(order);
            log.warn("⚠️ Order {} CANCELED after {} failed payment attempts", order.getOrderId(), failedAttempts);
            // TODO: Notify customer about order cancellation
        } else {
            log.info("Payment can be retried. {} of {} attempts used", failedAttempts, MAX_PAYMENT_ATTEMPTS);
            // TODO: Notify customer to retry payment
        }
    }

    /**
     * Save new customer payment method
     * Assumes the payment method doesn't already exist (caller should check)
     */
    private CustomerPaymentMethod saveCustomerPaymentMethod(
            Integer customerId,
            com.stripe.model.PaymentMethod stripePaymentMethod) {

        CustomerPaymentMethod newMethod = new CustomerPaymentMethod();

        // Set customer
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));
        newMethod.setCustomer(customer);

        // Set Stripe payment method ID
        newMethod.setStripeMethodId(stripePaymentMethod.getId());

        // Extract card details if payment method is a card
        if ("card".equals(stripePaymentMethod.getType()) && stripePaymentMethod.getCard() != null) {
            var card = stripePaymentMethod.getCard();
            newMethod.setCardBrand(card.getBrand());
            newMethod.setLast4(card.getLast4());
            newMethod.setExpMonth(Math.toIntExact(card.getExpMonth()));
            newMethod.setExpYear(Math.toIntExact(card.getExpYear()));
        }

        // Set as non-default by default (customer can change later)
        newMethod.setIsDefault(false);

        return customerPaymentMethodRepository.save(newMethod);
    }

    /**
     * Map Stripe PaymentMethod to our PaymentMethod entity
     * Stripe payment method types: card, us_bank_account, sepa_debit, ideal, etc.
     * Our payment method types: Credit Card, Bank Transfer, PayPal, Cash, Other
     */
    private PaymentMethod mapStripePaymentMethodToOurs(com.stripe.model.PaymentMethod stripePaymentMethod) {
        String stripeType = stripePaymentMethod.getType();
        String ourType;

        // Map Stripe payment method type to our enum
        switch (stripeType) {
            case "card":
                ourType = "Credit Card";
                break;
            case "us_bank_account":
            case "sepa_debit":
            case "bacs_debit":
            case "au_becs_debit":
                ourType = "Bank Transfer";
                break;
            case "paypal":
                ourType = "PayPal";
                break;
            default:
                ourType = "Other";
                log.warn("Unknown Stripe payment method type: {}. Mapping to 'Other'", stripeType);
        }

        // Find or create payment method in database
        return paymentMethodRepository.findByName(ourType)
                .orElseGet(() -> {
                    // Create new payment method if it doesn't exist
                    PaymentMethod newMethod = new PaymentMethod();
                    newMethod.setType(ourType);
                    newMethod.setName(ourType); // Use type as name for simplicity
                    PaymentMethod saved = paymentMethodRepository.save(newMethod);
                    log.info("Created new payment method: {} ({})", saved.getName(), saved.getType());
                    return saved;
                });
    }
}
