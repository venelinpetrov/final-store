package com.vpe.finalstore.order.entities;

import com.vpe.finalstore.order.enums.OrderStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_statuses")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @NotNull
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private OrderStatusType name;

}