package com.vpe.finalstore.customer.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerDto {
    private Integer customerId;
    private String name;
    private String phone;
    private LocalDate dateOfBirth;
}
