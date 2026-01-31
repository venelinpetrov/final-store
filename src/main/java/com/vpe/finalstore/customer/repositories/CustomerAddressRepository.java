package com.vpe.finalstore.customer.repositories;

import com.vpe.finalstore.customer.entities.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
    List<CustomerAddress> findByCustomer_CustomerId(Integer customerId);

    Optional<CustomerAddress> findByAddressIdAndCustomer_CustomerId(Integer addressId, Integer customerId);

    Optional<CustomerAddress> findByCustomer_CustomerIdAndIsDefaultTrue(Integer customerId);
}
