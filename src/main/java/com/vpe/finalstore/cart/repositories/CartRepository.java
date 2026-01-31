package com.vpe.finalstore.cart.repositories;

import com.vpe.finalstore.cart.entities.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @EntityGraph(attributePaths = "cartItems.variant")
    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId")
    Optional<Cart> getCartWithItems(@Param("cartId") UUID cartId);

    @EntityGraph(attributePaths = "cartItems.variant")
    @Query("SELECT c FROM Cart c WHERE c.sessionId = :sessionId")
    Optional<Cart> findBySessionId(@Param("sessionId") UUID sessionId);

    @EntityGraph(attributePaths = "cartItems.variant")
    @Query("SELECT c FROM Cart c WHERE c.customer.customerId = :customerId")
    Optional<Cart> findByCustomer_CustomerId(@Param("customerId") Integer customerId);
}