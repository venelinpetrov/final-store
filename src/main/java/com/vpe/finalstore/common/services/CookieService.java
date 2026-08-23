package com.vpe.finalstore.common.services;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.auth.config.CookieConfig;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CookieService {
    private final CookieConfig cookieConfig;

    public Cookie getCookie(String name, String value, String path, int maxAge) {
        var cookie = new Cookie(name, value);

        cookie.setHttpOnly(true);
        cookie.setPath(path);
        cookie.setSecure(cookieConfig.isSecure());
        cookie.setAttribute("SameSite", cookieConfig.getSameSite());
        cookie.setMaxAge(maxAge);

        return cookie;
    }
}
