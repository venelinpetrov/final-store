package com.vpe.finalstore.users;

import com.vpe.finalstore.common.SecurityRules;
import static com.vpe.finalstore.users.enums.RoleEnum.ADMIN;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityRules implements SecurityRules {

    @Override
    public void configure(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry.requestMatchers(HttpMethod.POST, "/api/users").hasAuthority(ADMIN.authority());
        registry.requestMatchers(HttpMethod.PUT, "/api/users/*").authenticated();
    }
}
