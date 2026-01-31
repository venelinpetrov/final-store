package com.vpe.finalstore.customer.mappers;

import com.vpe.finalstore.customer.dtos.CustomerAddressDto;
import com.vpe.finalstore.customer.entities.CustomerAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerAddressMapper {
    @Mapping(target = "customerId", source = "customer.customerId")
    @Mapping(target = "addressTypeId", source = "addressType.addressTypeId")
    @Mapping(target = "addressTypeName", source = "addressType.typeName")
    CustomerAddressDto toDto(CustomerAddress address);

    List<CustomerAddressDto> toDto(List<CustomerAddress> addresses);
}

