package com.vpe.finalstore.order.entities;

import com.vpe.finalstore.product.entities.ProductVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @EmbeddedId
    private OrderItemId orderItemId;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @MapsId("variantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sku")
    private String sku;

    @Column(name = "brand_name")
    private String brandName;

    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

}