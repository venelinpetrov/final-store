package com.vpe.finalstore.discount.security;

import com.vpe.finalstore.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;
import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;
import static com.vpe.finalstore.users.enums.RoleEnum.MERCHANT;

@Component
public class DiscountSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            // Discount endpoints
            .requestMatchers(HttpMethod.GET, "/api/discounts").hasAnyAuthority(MERCHANT.authority(), ADMIN.authority())
            .requestMatchers(HttpMethod.GET, "/api/discounts/applied").hasAnyAuthority(MERCHANT.authority(), ADMIN.authority())
            .requestMatchers(HttpMethod.POST, "/api/discounts").hasAnyAuthority(MERCHANT.authority(), ADMIN.authority())

            // Coupon endpoints
            .requestMatchers(HttpMethod.POST, "/api/coupons").hasAnyAuthority(MERCHANT.authority(), ADMIN.authority())
            .requestMatchers(HttpMethod.GET, "/api/coupons/validate/*").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/carts/*/coupon").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/carts/*/coupon").authenticated();
    }
}
