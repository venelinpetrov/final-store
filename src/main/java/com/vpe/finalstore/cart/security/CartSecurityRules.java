package com.vpe.finalstore.cart.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class CartSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            // Anonymous cart operations (no authentication required)
            .requestMatchers(HttpMethod.GET, "/api/carts/{cartId}").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/carts/session/*").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/carts").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/carts/{cartId}/items").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/carts/{cartId}/items/*").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/carts/{cartId}/items/*").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/carts/{cartId}/items").permitAll()
            
            // Authenticated cart operations (require login)
            .requestMatchers(HttpMethod.GET, "/api/carts/my-cart").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/carts/associate").authenticated();
    }
}

