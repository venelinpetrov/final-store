package com.vpe.finalstore.cart.entities;

import com.vpe.finalstore.customer.entities.Customer;
import com.vpe.finalstore.product.entities.ProductVariant;
import org.hibernate.annotations.Generated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "session_id")
    private UUID sessionId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "created_at", insertable = false, updatable = false)
    @Generated
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @Generated
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public CartItem getItem(Integer variantId) {
        return cartItems.stream()
            .filter(item -> item.getVariant().getVariantId().equals(variantId))
            .findFirst()
            .orElse(null);
    }

    public CartItem addItem(ProductVariant variant) {
        var cartItem = getItem(variant.getVariantId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setVariant(variant);
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            cartItems.add(cartItem);
        }

        return cartItem;
    }


    public void removeItem(Integer variantId) {
        var cartItem = getItem(variantId);

        if (cartItem != null) {
            cartItems.remove(cartItem);
            cartItem.setCart(null);
        }
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public void clear() {
        cartItems.clear();
    }

}