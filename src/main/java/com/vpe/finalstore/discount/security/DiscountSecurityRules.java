package com.vpe.finalstore.discount.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;

@Component
public class DiscountSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(HttpMethod.GET, "/api/discounts").hasAuthority(ADMIN.authority()) // TODO Make Merchant role
            .requestMatchers(HttpMethod.GET, "/api/discounts/applied").hasAuthority(ADMIN.authority())
            .requestMatchers(HttpMethod.POST, "/api/discounts").hasAuthority(ADMIN.authority());
    }
}
