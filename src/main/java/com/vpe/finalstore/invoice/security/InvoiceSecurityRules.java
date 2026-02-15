package com.vpe.finalstore.invoice.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class InvoiceSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(HttpMethod.GET, "/api/invoices/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/invoices/customers/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/invoices/orders/*").authenticated();
    }
}

