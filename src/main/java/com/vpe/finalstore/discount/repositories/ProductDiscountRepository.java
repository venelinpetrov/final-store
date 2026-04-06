package com.vpe.finalstore.discount.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vpe.finalstore.discount.entities.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {

	@Query("""
		SELECT d FROM ProductDiscount d
		WHERE d.productVariant.variantId = :variantId
		AND :now BETWEEN d.validFrom AND d.validUntil
	""")
	Optional<ProductDiscount> findActiveDiscount(
		@Param("variantId") Integer variantId,
		@Param("now") LocalDateTime now
	);

	@Query("""
		SELECT d FROM ProductDiscount d
		WHERE d.productVariant.variantId IN :variantIds
		AND :now BETWEEN d.validFrom AND d.validUntil
	""")
	List<ProductDiscount> findActiveDiscounts(
		@Param("variantIds") List<Integer> variantIds,
		@Param("now") LocalDateTime now
	);
}
