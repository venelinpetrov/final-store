package com.vpe.finalstore.order.repositories;

import com.vpe.finalstore.order.entities.OrderStatus;
import com.vpe.finalstore.order.enums.OrderStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    Optional<OrderStatus> findByName(OrderStatusType name);
}
