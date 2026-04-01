package com.vpe.finalstore.shipment.entities;

import com.vpe.finalstore.customer.entities.CustomerAddress;
import com.vpe.finalstore.order.entities.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Integer shipmentId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @NotNull
    @Column(name = "tracking_number")
    private String trackingNumber;

    @NotNull
    @Column(name = "shipment_date")
    private LocalDateTime shipmentDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private CustomerAddress address;

    @OneToMany(mappedBy = "shipment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<ShipmentItem> shipmentItems = new LinkedHashSet<>();

}
