package com.vpe.finalstore.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class StripeConfig {

    @Value("${stripe.api.secret-key:}")
    private String secretKey;

    @Value("${stripe.api.publishable-key:}")
    private String publishableKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            log.warn("Stripe API key not configured. Stripe functionality will be disabled.");
            return;
        }

        Stripe.apiKey = this.secretKey;
        log.info("Stripe API initialized successfully");
    }
}

