package com.vpe.finalstore.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "variant_option_assignments")
public class ProductVariantOptionAssignment {
    public ProductVariantOptionAssignment(ProductVariant variant, ProductVariantOptionValue value) {
        this.id = new ProductVariantOptionAssignmentId();
        this.variant = variant;
        this.value = value;
    }

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
