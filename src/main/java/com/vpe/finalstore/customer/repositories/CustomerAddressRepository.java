package com.vpe.finalstore.customer.repositories;

import com.vpe.finalstore.customer.entities.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
}
