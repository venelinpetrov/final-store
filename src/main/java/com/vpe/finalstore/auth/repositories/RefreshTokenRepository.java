package com.vpe.finalstore.auth.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vpe.finalstore.auth.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByTokenHash(String hashCode);

    @Modifying
    @Query("""
        UPDATE RefreshToken r
        SET r.revokedAt = :now
        WHERE r.tokenHash = :tokenHash
          AND r.revokedAt IS NULL
    """)
    int revokeIfActive(
        @Param("tokenHash") String tokenHash,
        @Param("now") Instant now
    );
}
