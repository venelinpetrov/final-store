package com.vpe.finalstore.shipment.entities;

import com.vpe.finalstore.shipment.enums.ShipmentStatusType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shipment_statuses")
public class ShipmentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @NotNull
    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private ShipmentStatusType name;
}
