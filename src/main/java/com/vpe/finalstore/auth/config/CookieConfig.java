package com.vpe.finalstore.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.cookie")
@Data
public class CookieConfig {
    private String sameSite;
    private boolean secure;
}
