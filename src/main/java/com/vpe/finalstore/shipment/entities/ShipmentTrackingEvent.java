package com.vpe.finalstore.shipment.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shipment_tracking_events")
public class ShipmentTrackingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private ShipmentStatus status;

    @NotNull
    @Column(name = "event_date", insertable = false, updatable = false)
    private LocalDateTime eventDate;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;
}
