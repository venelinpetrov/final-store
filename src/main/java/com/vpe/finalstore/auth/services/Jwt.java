package com.vpe.finalstore.auth.services;

import io.jsonwebtoken.Claims;

public class Jwt {
    private final Claims claims;
    private final String token;

    public Jwt(Claims claims, String token) {
        this.claims = claims;
        this.token = token;
    }

    public Integer getUserId() {
        return Integer.valueOf(claims.getSubject());
    }

    public String getRole() {
        return claims.get("role", String.class);
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new java.util.Date());
    }

    @Override
    public String toString() {
        return token;
    }
}
