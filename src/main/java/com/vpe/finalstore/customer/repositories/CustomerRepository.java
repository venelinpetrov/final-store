package com.vpe.finalstore.customer.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.vpe.finalstore.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> getCustomerByCustomerId(Integer customerId);
}
