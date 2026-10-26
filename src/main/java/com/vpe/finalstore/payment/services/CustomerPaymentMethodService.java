package com.vpe.finalstore.payment.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vpe.finalstore.auth.config.AuthService;
import com.vpe.finalstore.payment.dtos.CustomerPaymentMethodDto;
import com.vpe.finalstore.payment.mappers.CustomerPaymentMethodMapper;
import com.vpe.finalstore.payment.repositories.CustomerPaymentMethodRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class CustomerPaymentMethodService {
    private final CustomerPaymentMethodRepository customerPaymentMethodRepository;
    private final AuthService authService;
    private final CustomerPaymentMethodMapper customerPaymentMethodMapper;

    @Transactional
    public List<CustomerPaymentMethodDto> getPaymentMethods() {
        var user = authService.getCurrentUser();

        if (user == null) {
            return List.of();
        }

        var customerId = user.getCustomer().getCustomerId();
        var paymentMethods = customerPaymentMethodRepository.findByCustomer_CustomerId(customerId);

        return customerPaymentMethodMapper.toDto(paymentMethods);
    }
}
