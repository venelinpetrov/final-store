package com.vpe.finalstore.discount.security;

import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

import com.vpe.finalstore.common.SecurityRules;

@Component
public class ProductDiscountSecurity implements SecurityRules {

	@Override
	public void configure(
		AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
	) {
		registry.requestMatchers(HttpMethod.POST, "/api/product-discounts").hasAuthority(ADMIN.authority());
	}
}
