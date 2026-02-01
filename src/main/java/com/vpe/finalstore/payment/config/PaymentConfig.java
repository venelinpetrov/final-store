package com.vpe.finalstore.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {
    
    /**
     * Currency code for payments (e.g., "eur", "usd", "gbp")
     * Default: eur
     */
    private String currency = "eur";
}

