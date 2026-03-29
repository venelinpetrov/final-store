package com.vpe.finalstore.shipment.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;

@Component
public class ShipmentSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry.requestMatchers(HttpMethod.GET, "/api/carriers").permitAll();
        registry.requestMatchers(HttpMethod.POST, "/api/carriers").hasAuthority(ADMIN.authority());
    }
}
