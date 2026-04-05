package com.vpe.finalstore.discount.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vpe.finalstore.product.entities.ProductVariant;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_discounts")
public class ProductDiscount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "discount_id")
	private Integer discountId;

	@NonNull
	@Column(name = "discount_percentage")
	private BigDecimal discountPercentage = BigDecimal.ZERO;

	@NonNull
	@Column(name = "valid_from")
	private LocalDateTime validFrom;

	@NonNull
	@Column(name = "valid_until")
	private LocalDateTime validUntil;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id")
	private ProductVariant productVariant;

}
