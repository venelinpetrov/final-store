package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
        SELECT c FROM Coupon c
        JOIN FETCH c.discount d
        WHERE c.code = :code
            AND c.isActive = true
            AND (c.usageLimit IS NULL OR c.timesUsed < c.usageLimit)
            AND d.isActive = true
            AND (d.validFrom IS NULL OR d.validFrom <= CURRENT_TIMESTAMP)
            AND (d.validUntil IS NULL OR d.validUntil >= CURRENT_TIMESTAMP)
    """)
    Optional<Coupon> findValidCouponByCode(@Param("code") String code);
}