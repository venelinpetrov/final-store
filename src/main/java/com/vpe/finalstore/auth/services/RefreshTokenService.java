package com.vpe.finalstore.auth.services;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.security.MessageDigest;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.auth.entities.RefreshToken;
import com.vpe.finalstore.auth.repositories.RefreshTokenRepository;
import com.vpe.finalstore.exceptions.UnauthorizedException;
import com.vpe.finalstore.users.entities.User;
import com.vpe.finalstore.users.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public TokenPair rotate(String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);

        if (jwt == null || jwt.isExpired()) {
            throw new UnauthorizedException();
        }

        var hash = hashToken(refreshToken);

        var updated = refreshTokenRepository.revokeIfActive(
            hash,
            Instant.now()
        );

        if (updated != 1) {
            throw new UnauthorizedException();
        }

        var user = userRepository.findById(jwt.getUserId())
            .orElseThrow(UnauthorizedException::new);

        var accessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        saveToken(newRefreshToken, user);

        return new TokenPair(accessToken, newRefreshToken);
    }

    public void saveToken(Jwt jwt, User user) {
        var refreshToken = new RefreshToken();

        refreshToken.setTokenHash(hashToken(jwt.toString()));
        refreshToken.setUserId(user.getUserId());
        refreshToken.setExpiresAt(jwt.getExpiration());

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeToken(String token) {
        var hash = hashToken(token);
        var updated = refreshTokenRepository.revokeIfActive(hash, Instant.now());

        if (updated != 1) {
            throw new UnauthorizedException();
        }
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
