package com.vpe.finalstore.customer.controllers;

import com.vpe.finalstore.customer.dtos.CustomerDto;
import com.vpe.finalstore.customer.mappers.CustomerMapper;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customers")
class CustomerController {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Operation(
        summary = "Get customer by ID"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Integer customerId) {
        var customer = customerRepository.getCustomerByCustomerId(customerId).orElseThrow();

        return ResponseEntity.ok(customerMapper.toDto(customer));
    }
}
