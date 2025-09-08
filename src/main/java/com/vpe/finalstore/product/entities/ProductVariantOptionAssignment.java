package com.vpe.finalstore.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "variant_option_assignments")
public class ProductVariantOptionAssignment {
    @EmbeddedId
    private ProductVariantOptionAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("valueId")
    @JoinColumn(name = "value_id")
    private ProductVariantOptionValue value;
}
