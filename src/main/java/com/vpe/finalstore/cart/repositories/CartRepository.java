package com.vpe.finalstore.cart.repositories;

import com.vpe.finalstore.cart.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @EntityGraph(attributePaths = "cartItems.variant")
    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId")
    Optional<Cart> getCartWithItems(@Param("cartId") Integer cartId);
}