package com.vpe.finalstore.customer.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerAddressUpdateDto {
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;

    @Size(max = 2, message = "Country code must be 2 characters")
    private String countryCode;

    @Size(max = 100, message = "State must be less than 100 characters")
    private String state;

    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @Size(max = 100, message = "Street must be less than 100 characters")
    private String street;

    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;

    @Size(max = 10, message = "Floor must be less than 10 characters")
    private String floor;

    @Size(max = 10, message = "Apartment number must be less than 10 characters")
    private String apartmentNo;

    private Integer addressTypeId;

    private Boolean isDefault;
}

