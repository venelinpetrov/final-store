package com.vpe.finalstore.order.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class OrderSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/orders/from-cart/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/orders/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/orders/customers/*").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").authenticated();
    }
}
