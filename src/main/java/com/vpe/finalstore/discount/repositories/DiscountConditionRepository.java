package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.DiscountCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountConditionRepository extends JpaRepository<DiscountCondition, Integer> {
}