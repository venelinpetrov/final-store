package com.vpe.finalstore.payment.mappers;

import com.vpe.finalstore.payment.dtos.PaymentDto;
import com.vpe.finalstore.payment.entities.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "invoiceId", source = "invoice.invoiceId")
    @Mapping(target = "methodName", source = "method.name")
    @Mapping(target = "status", source = "status.name")
    PaymentDto toDto(Payment payment);

    List<PaymentDto> toDto(List<Payment> payments);
}

