package com.vpe.finalstore.customer.security;

import com.vpe.finalstore.customer.repositories.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("customerSecurity")
public class CustomerSecurity {
    private final CustomerRepository customerRepository;

    public CustomerSecurity(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean isOwner(Integer customerId, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();

        return customerRepository
            .getCustomerByCustomerId(customerId)
            .map(c -> c.getUser().getUserId().equals(userId))
            .orElse(false);
    }
}