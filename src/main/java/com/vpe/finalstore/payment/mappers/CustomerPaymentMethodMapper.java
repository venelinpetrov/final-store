package com.vpe.finalstore.payment.mappers;

import com.vpe.finalstore.payment.dtos.CustomerPaymentMethodDto;
import com.vpe.finalstore.payment.entities.CustomerPaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerPaymentMethodMapper {
    @Mapping(target = "customerId", source = "customer.customerId")
    CustomerPaymentMethodDto toDto(CustomerPaymentMethod entity);

    List<CustomerPaymentMethodDto> toDto(List<CustomerPaymentMethod> entities);
}

