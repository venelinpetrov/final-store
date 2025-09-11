package com.vpe.finalstore.product.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "product_variant_image_assignments")
public class ProductVariantImageAssignment {
    public ProductVariantImageAssignment(ProductVariant variant, ProductImage image, Boolean isPrimary) {
        this.id = new ProductVariantImageAssignmentId();
        this.variant = variant;
        this.image = image;
        this.isPrimary = isPrimary;
    }

    @EmbeddedId
    private ProductVariantImageAssignmentId id;

    @MapsId("variantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "image_id")
    private ProductImage image;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariantImageAssignment that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}