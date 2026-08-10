package com.vpe.finalstore.auth.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.security.MessageDigest;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.auth.entities.RefreshToken;
import com.vpe.finalstore.auth.repositories.RefreshTokenRepository;
import com.vpe.finalstore.exceptions.UnauthorizedException;
import com.vpe.finalstore.users.entities.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveToken(Jwt jwt, User user) {
        var refreshToken = new RefreshToken();
        var hashCode = hashToken(jwt.toString());

        System.out.println("save>jwt: " + jwt.toString() + " hashCode:" + hashCode);

        refreshToken.setTokenHash(hashCode);
        refreshToken.setUserId(user.getUserId());
        refreshToken.setExpiresAt(jwt.getExpiration());

        refreshTokenRepository.save(refreshToken);
    }

    public void revokeToken(Jwt jwt) {
        var hashCode = hashToken(jwt.toString());
        System.out.println("revoke>jwt: " + jwt.toString() + " hashCode:" + hashCode);
        var storedToken = refreshTokenRepository.findByTokenHash(hashCode)
            .orElseThrow(UnauthorizedException::new);

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new UnauthorizedException();
        }

        storedToken.revoke();

        refreshTokenRepository.save(storedToken);
    }

    private String hashToken(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
