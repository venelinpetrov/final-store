package com.vpe.finalstore.discount.entities;

import com.vpe.finalstore.discount.enums.DiscountConditionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "discount_conditions")
public class DiscountCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id", nullable = false)
    private Integer conditionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private DiscountConditionType conditionType;

    @Column(name = "decimal_value", precision = 10, scale = 2)
    private BigDecimal decimalValue;

    @Column(name = "int_value")
    private Integer intValue;

    @Size(max = 255)
    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}