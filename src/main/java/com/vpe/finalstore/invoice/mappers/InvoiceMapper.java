package com.vpe.finalstore.invoice.mappers;

import com.vpe.finalstore.invoice.dtos.InvoiceDto;
import com.vpe.finalstore.invoice.entities.Invoice;
import com.vpe.finalstore.payment.mappers.PaymentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentMapper.class)
public interface InvoiceMapper {
    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "customerId", source = "customer.customerId")
    InvoiceDto toDto(Invoice invoice);
}

