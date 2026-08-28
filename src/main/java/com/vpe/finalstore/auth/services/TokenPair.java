package com.vpe.finalstore.auth.services;

public record TokenPair(Jwt accessToken, Jwt refreshToken) {}