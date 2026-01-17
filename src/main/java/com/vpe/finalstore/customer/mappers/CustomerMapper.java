package com.vpe.finalstore.customer.mappers;

import com.vpe.finalstore.customer.dtos.CustomerDto;
import com.vpe.finalstore.customer.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer customer);
}
