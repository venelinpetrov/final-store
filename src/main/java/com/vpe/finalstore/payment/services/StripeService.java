package com.vpe.finalstore.payment.services;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.vpe.finalstore.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class StripeService {
    public PaymentIntent createPaymentIntent(
            BigDecimal amount,
            String currency,
            String customerId,
            String paymentMethodId,
            Map<String, String> metadata
    ) {
        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(new BigDecimal("100")).longValue();

            var paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency);

            if (customerId != null && !customerId.isEmpty()) {
                paramsBuilder.setCustomer(customerId);
            }

            if (paymentMethodId != null && !paymentMethodId.isEmpty()) {
                paramsBuilder.setPaymentMethod(paymentMethodId);
            }

            if (metadata != null && !metadata.isEmpty()) {
                paramsBuilder.putAllMetadata(metadata);
            }

            // Automatic payment methods (card, etc.)
            paramsBuilder.setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
            );

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            log.info("Created PaymentIntent: {}", paymentIntent.getId());
            return paymentIntent;

        } catch (StripeException e) {
            log.error("Failed to create PaymentIntent: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to create payment: " + e.getMessage());
        }
    }

    public PaymentIntent confirmPaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntent confirmedIntent = paymentIntent.confirm();
            log.info("Confirmed PaymentIntent: {}", confirmedIntent.getId());
            return confirmedIntent;

        } catch (StripeException e) {
            log.error("Failed to confirm PaymentIntent {}: {}", paymentIntentId, e.getMessage(), e);
            throw new BadRequestException("Failed to confirm payment: " + e.getMessage());
        }
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentIntent {}: {}", paymentIntentId, e.getMessage(), e);
            throw new BadRequestException("Failed to retrieve payment: " + e.getMessage());
        }
    }

    public Customer createCustomer(String email, String name, Map<String, String> metadata) {
        try {
            var paramsBuilder = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name);

            if (metadata != null && !metadata.isEmpty()) {
                paramsBuilder.putAllMetadata(metadata);
            }

            Customer customer = Customer.create(paramsBuilder.build());
            log.info("Created Stripe Customer: {}", customer.getId());
            return customer;

        } catch (StripeException e) {
            log.error("Failed to create Stripe Customer: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to create customer: " + e.getMessage());
        }
    }

    public Customer retrieveCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            log.error("Failed to retrieve Stripe Customer {}: {}", customerId, e.getMessage(), e);
            throw new BadRequestException("Failed to retrieve customer: " + e.getMessage());
        }
    }

    public PaymentMethod attachPaymentMethod(String paymentMethodId, String customerId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();

            PaymentMethod attachedMethod = paymentMethod.attach(params);
            log.info("Attached PaymentMethod {} to Customer {}", paymentMethodId, customerId);
            return attachedMethod;

        } catch (StripeException e) {
            log.error("Failed to attach PaymentMethod {} to Customer {}: {}",
                    paymentMethodId, customerId, e.getMessage(), e);
            throw new BadRequestException("Failed to attach payment method: " + e.getMessage());
        }
    }

    public PaymentMethod detachPaymentMethod(String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            PaymentMethod detachedMethod = paymentMethod.detach();
            log.info("Detached PaymentMethod {}", paymentMethodId);
            return detachedMethod;

        } catch (StripeException e) {
            log.error("Failed to detach PaymentMethod {}: {}", paymentMethodId, e.getMessage(), e);
            throw new BadRequestException("Failed to detach payment method: " + e.getMessage());
        }
    }

    public PaymentMethod retrievePaymentMethod(String paymentMethodId) {
        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (StripeException e) {
            log.error("Failed to retrieve PaymentMethod {}: {}", paymentMethodId, e.getMessage(), e);
            throw new BadRequestException("Failed to retrieve payment method: " + e.getMessage());
        }
    }
}

