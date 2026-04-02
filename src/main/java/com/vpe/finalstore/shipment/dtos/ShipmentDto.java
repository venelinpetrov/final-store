package com.vpe.finalstore.shipment.dtos;

import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentDto {
    private Integer shipmentId;
    private Integer carrierId;
    private String trackingNumber;
    private LocalDateTime shipmentDate;
    private LocalDateTime deliveryDate;
    private Integer orderId;
    private ShipmentStatusType status;
}
