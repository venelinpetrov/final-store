package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
}