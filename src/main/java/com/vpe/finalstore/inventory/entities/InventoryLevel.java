package com.vpe.finalstore.inventory.entities;

import com.vpe.finalstore.product.entities.ProductVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventory_levels")
public class InventoryLevel {
    public InventoryLevel(ProductVariant variant, Integer quantityInStock) {
        this.variant = variant;
        this.quantityInStock = quantityInStock;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}