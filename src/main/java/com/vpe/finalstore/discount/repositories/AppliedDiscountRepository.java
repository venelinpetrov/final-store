package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.AppliedDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppliedDiscountRepository extends JpaRepository<AppliedDiscount, Integer> {
}