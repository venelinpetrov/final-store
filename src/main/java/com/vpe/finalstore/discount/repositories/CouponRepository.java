package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
}