package com.vpe.finalstore.payment.controllers;

import com.vpe.finalstore.payment.dtos.CustomerPaymentMethodDto;
import com.vpe.finalstore.payment.services.CustomerPaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customer-payment-methods")
public class CustomerPaymentMethodController {
    private final CustomerPaymentMethodService customerPaymentMethodService;

    @Operation(
        summary = "Get customer payment methods"
    )
    @GetMapping
    public ResponseEntity<List<CustomerPaymentMethodDto>> getPayment() {
        return ResponseEntity.ok(
            customerPaymentMethodService.getPaymentMethods()
        );
    }
}

