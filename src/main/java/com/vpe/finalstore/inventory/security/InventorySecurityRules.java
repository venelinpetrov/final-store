package com.vpe.finalstore.inventory.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;

@Component
public class InventorySecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(HttpMethod.GET, "/api/inventory/levels/**").hasAuthority(ADMIN.authority())
            .requestMatchers(HttpMethod.GET, "/api/inventory/levels").hasAuthority(ADMIN.authority())
            .requestMatchers(HttpMethod.GET, "/api/inventory-movements").hasAuthority(ADMIN.authority())
            .requestMatchers(HttpMethod.POST, "/api/inventory-movements").hasAuthority(ADMIN.authority());
    }
}
