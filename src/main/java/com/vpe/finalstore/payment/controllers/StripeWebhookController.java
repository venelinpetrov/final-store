
package com.vpe.finalstore.payment.controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.vpe.finalstore.payment.config.StripeConfig;
import com.vpe.finalstore.payment.services.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;

        // Verify webhook signature
        try {
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    stripeConfig.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            log.error("⚠️ Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("⚠️ Webhook error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

        // Handle the event
        log.info("🔔 Received Stripe webhook event: {} (ID: {})", event.getType(), event.getId());

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
                case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
                // case "charge.refunded" -> handleChargeRefunded(event);
                // case "charge.dispute.created" -> handleDisputeCreated(event);
                default -> log.info("Unhandled event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing webhook event {}: {}", event.getType(), e.getMessage(), e);
            // Return 200 anyway to prevent Stripe from retrying
            // Log the error for manual investigation
            return ResponseEntity.ok("Error logged");
        }

        return ResponseEntity.ok("Success");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        var dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isEmpty()) {
            log.warn("Could not deserialize PaymentIntent from event");
            return;
        }

        var paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
        log.info("✅ Payment succeeded: {}", paymentIntent.getId());

        // Extract metadata
        String cartId = paymentIntent.getMetadata().get("cartId");
        String customerId = paymentIntent.getMetadata().get("customerId");
        String addressId = paymentIntent.getMetadata().get("addressId");

        if (cartId == null || customerId == null || addressId == null) {
            log.error("Missing required metadata in PaymentIntent {}: cartId={}, customerId={}, addressId={}",
                paymentIntent.getId(), cartId, customerId, addressId);
            return;
        }

        try {
            paymentService.handlePaymentSucceeded(
                    paymentIntent.getId(),
                    java.util.UUID.fromString(cartId),
                    Integer.parseInt(customerId),
                    Integer.parseInt(addressId)
            );
        } catch (Exception e) {
            log.error("Failed to process successful payment {}: {}", paymentIntent.getId(), e.getMessage(), e);
            throw e; // Re-throw to be caught by outer try-catch
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        var dataObjectDeserializer = event.getDataObjectDeserializer();
        if (dataObjectDeserializer.getObject().isEmpty()) {
            log.warn("Could not deserialize PaymentIntent from event");
            return;
        }

        var paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();
        log.warn("❌ Payment failed: {} - Reason: {}",
                paymentIntent.getId(),
                paymentIntent.getLastPaymentError() != null ?
                        paymentIntent.getLastPaymentError().getMessage() : "Unknown");

        paymentService.handlePaymentFailed(paymentIntent.getId());
    }
}

