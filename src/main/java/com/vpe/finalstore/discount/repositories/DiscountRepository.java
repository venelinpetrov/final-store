package com.vpe.finalstore.discount.repositories;

import com.vpe.finalstore.discount.entities.Discount;
import com.vpe.finalstore.discount.enums.DiscountConditionType;
import com.vpe.finalstore.discount.enums.DiscountScopeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
	@Query("""
		SELECT d FROM Discount d
		WHERE d.isActive = :isActive
			AND (:validFrom IS NULL OR d.validFrom IS NULL OR d.validFrom <= :validFrom)
			AND (:validUntil IS NULL OR d.validUntil IS NULL OR d.validUntil >= :validUntil)
			ORDER BY
			CASE WHEN d.validFrom IS NULL THEN 1 ELSE 0 END,
			d.validFrom DESC,
			d.createdAt DESC
	""")
	Page<Discount> getDiscounts(
		@Param("isActive") Boolean isActive,
		@Param("validFrom") LocalDateTime validFrom,
		@Param("validUntil") LocalDateTime validUntil,
		Pageable pageable
	);

	@Query("""
		SELECT d FROM Discount d
		JOIN d.discountConditions dc
		WHERE d.scope = :scope
			AND dc.conditionType = :conditionType
			AND dc.intValue = :variantId
			AND d.isActive = true
			AND (d.validFrom IS NULL OR d.validFrom <= CURRENT_TIMESTAMP)
			AND (d.validUntil IS NULL OR d.validUntil >= CURRENT_TIMESTAMP)
		ORDER BY
			CASE WHEN d.validFrom IS NULL THEN 1 ELSE 0 END,
			d.validFrom DESC,
			d.createdAt DESC
	""")
	List<Discount> findActiveDiscountsForVariant(
		@Param("variantId") Integer variantId,
		@Param("scope") DiscountScopeType scope,
		@Param("conditionType") DiscountConditionType conditionType
	);
	default Optional<Discount> findActiveDiscountForVariant(Integer variantId) {
		List<Discount> discounts = findActiveDiscountsForVariant(
			variantId,
			DiscountScopeType.VARIANT,
			DiscountConditionType.VARIANT
		);
		return discounts.isEmpty() ? Optional.empty() : Optional.of(discounts.get(0));
	}

	@Query("""
		SELECT d FROM Discount d
		WHERE d.scope = :scope
			AND d.minOrderAmount <= :minOrderAmount
			AND d.isActive = true
			AND (d.validFrom IS NULL OR d.validFrom <= CURRENT_TIMESTAMP)
			AND (d.validUntil IS NULL OR d.validUntil >= CURRENT_TIMESTAMP)
		ORDER BY
			CASE WHEN d.validFrom IS NULL THEN 1 ELSE 0 END,
			d.validFrom DESC,
			d.createdAt DESC
	""")
	List<Discount> findActiveDiscountsForOrder(
		@Param("scope") DiscountScopeType scope,
		@Param("minOrderAmount") BigDecimal minOrderAmount
	);
	default Optional<Discount> findActiveDiscountForOrder(BigDecimal minOrderAmount) {
		List<Discount> discounts = findActiveDiscountsForOrder(DiscountScopeType.ORDER, minOrderAmount
		);
		return discounts.isEmpty() ? Optional.empty() : Optional.of(discounts.get(0));
	}
}