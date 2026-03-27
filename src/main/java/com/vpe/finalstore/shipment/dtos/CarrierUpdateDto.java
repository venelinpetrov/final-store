package com.vpe.finalstore.shipment.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrierUpdateDto {

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(min = 6, max = 100)
    private String code;

    @Size(max = 500)
    private String trackingUrlTemplate;

    @Size(max = 500)
    private String apiEndpoint;
}
