package com.vpe.finalstore.customer.repositories;

import com.vpe.finalstore.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
