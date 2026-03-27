package com.vpe.finalstore.shipment.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentCreateDto {
    @NotNull
    private Integer carrierId;

    @NotNull
    @Size(max = 50)
    private String trackingNumber;

    @NotNull
    private LocalDateTime shipmentDate;

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer addressId;
}
