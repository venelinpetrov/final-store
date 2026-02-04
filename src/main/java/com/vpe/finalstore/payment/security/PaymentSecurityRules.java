package com.vpe.finalstore.payment.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class PaymentSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(HttpMethod.POST, "/api/payments/create").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/payments/*/complete").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/payments/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/payments/verify/*").authenticated();
    }
}

