package com.vpe.finalstore.auth.services;

import io.jsonwebtoken.Claims;

import java.util.List;

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

    public List<String> getRoles() {
        Object raw = claims.get("roles");
        if (raw instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new java.util.Date());
    }

    public Integer getTokenVersion() {
        return (Integer) claims.get("ver");
    }

    @Override
    public String toString() {
        return token;
    }
}
