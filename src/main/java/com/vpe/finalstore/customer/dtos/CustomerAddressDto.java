package com.vpe.finalstore.customer.dtos;

import lombok.Data;

@Data
public class CustomerAddressDto {
    private Integer addressId;
    private Integer customerId;
    private String country;
    private String state;
    private String city;
    private String street;
    private String floor;
    private String apartmentNo;
    private Integer addressTypeId;
    private String addressTypeName;
    private Boolean isDefault;
}

