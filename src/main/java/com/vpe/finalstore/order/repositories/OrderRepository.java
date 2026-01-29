package com.vpe.finalstore.order.repositories;

import com.vpe.finalstore.order.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"orderItems", "status", "customer", "address"})
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findOrderWithDetails(@Param("orderId") Integer orderId);

    @EntityGraph(attributePaths = {"orderItems", "status"})
    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId")
    Page<Order> findByCustomerCustomerId(@Param("customerId") Integer customerId, Pageable pageable);
}
