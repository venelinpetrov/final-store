package com.vpe.finalstore.shipment.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrierDto {
    private Integer carrierId;
    private String name;
    private String code;
    private String trackingUrlTemplate;
    private String apiEndpoint;
}
