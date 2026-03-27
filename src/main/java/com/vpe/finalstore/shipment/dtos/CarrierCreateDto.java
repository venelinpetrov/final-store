package com.vpe.finalstore.shipment.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrierCreateDto {
    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(min = 2, max = 100)
    private String code;

    private String trackingUrlTemplate;

    private String apiEndpoint;
}
