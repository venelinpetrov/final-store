package com.vpe.finalstore.product.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductVariantOptionAssignmentId implements Serializable {
    private Integer variantId;
    private Integer valueId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ProductVariantOptionAssignmentId that)) {
            return false;
        }

        return Objects.equals(variantId, that.variantId)
               && Objects.equals(valueId, that.valueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId, valueId);
    }
}