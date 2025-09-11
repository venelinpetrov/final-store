package com.vpe.finalstore.product.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductVariantImageAssignmentId implements Serializable {
//    private static final long serialVersionUID = 6496691026739809062L;
    @NotNull
    @Column(name = "variant_id", nullable = false)
    private Integer variantId;

    @NotNull
    @Column(name = "image_id", nullable = false)
    private Integer imageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ProductVariantImageAssignmentId entity = (ProductVariantImageAssignmentId) o;
        return Objects.equals(this.imageId, entity.imageId) &&
               Objects.equals(this.variantId, entity.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, variantId);
    }
}