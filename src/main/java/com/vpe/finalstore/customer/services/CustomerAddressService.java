package com.vpe.finalstore.customer.services;

import com.vpe.finalstore.customer.dtos.CustomerAddressCreateDto;
import com.vpe.finalstore.customer.dtos.CustomerAddressDto;
import com.vpe.finalstore.customer.dtos.CustomerAddressUpdateDto;
import com.vpe.finalstore.customer.entities.CustomerAddress;
import com.vpe.finalstore.customer.mappers.CustomerAddressMapper;
import com.vpe.finalstore.customer.repositories.AddressTypeRepository;
import com.vpe.finalstore.customer.repositories.CustomerAddressRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerAddressService {
    private final CustomerAddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final AddressTypeRepository addressTypeRepository;
    private final CustomerAddressMapper addressMapper;

    public List<CustomerAddressDto> getAddressesByCustomerId(Integer customerId) {
        var addresses = addressRepository.findByCustomer_CustomerId(customerId);
        return addressMapper.toDto(addresses);
    }

    public CustomerAddressDto getAddressById(Integer customerId, Integer addressId) {
        var address = addressRepository.findByAddressIdAndCustomer_CustomerId(addressId, customerId)
            .orElseThrow(() -> new NotFoundException("Address not found or does not belong to customer"));

        return addressMapper.toDto(address);
    }

    @Transactional
    public CustomerAddressDto createAddress(Integer customerId, CustomerAddressCreateDto dto) {
        var customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new NotFoundException("Customer not found"));

        var addressType = addressTypeRepository.findById(dto.getAddressTypeId())
            .orElseThrow(() -> new NotFoundException("Address type not found"));

        var address = new CustomerAddress();
        address.setCustomer(customer);
        address.setCountry(dto.getCountry());
        address.setState(dto.getState());
        address.setCity(dto.getCity());
        address.setStreet(dto.getStreet());
        address.setFloor(dto.getFloor());
        address.setApartmentNo(dto.getApartmentNo());
        address.setAddressType(addressType);
        address.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        // If this is set as default, unset any existing default
        if (address.getIsDefault()) {
            unsetDefaultAddress(customerId);
        }

        var savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Transactional
    public CustomerAddressDto updateAddress(Integer customerId, Integer addressId, CustomerAddressUpdateDto dto) {
        var address = addressRepository.findByAddressIdAndCustomer_CustomerId(addressId, customerId)
            .orElseThrow(() -> new NotFoundException("Address not found or does not belong to customer"));

        if (dto.getCountry() != null) {
            address.setCountry(dto.getCountry());
        }
        if (dto.getState() != null) {
            address.setState(dto.getState());
        }
        if (dto.getCity() != null) {
            address.setCity(dto.getCity());
        }
        if (dto.getStreet() != null) {
            address.setStreet(dto.getStreet());
        }
        if (dto.getFloor() != null) {
            address.setFloor(dto.getFloor());
        }
        if (dto.getApartmentNo() != null) {
            address.setApartmentNo(dto.getApartmentNo());
        }
        if (dto.getAddressTypeId() != null) {
            var addressType = addressTypeRepository.findById(dto.getAddressTypeId())
                .orElseThrow(() -> new NotFoundException("Address type not found"));
            address.setAddressType(addressType);
        }
        if (dto.getIsDefault() != null) {
            if (dto.getIsDefault()) {
                unsetDefaultAddress(customerId);
            }
            address.setIsDefault(dto.getIsDefault());
        }

        var savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Transactional
    public void deleteAddress(Integer customerId, Integer addressId) {
        var address = addressRepository.findByAddressIdAndCustomer_CustomerId(addressId, customerId)
            .orElseThrow(() -> new NotFoundException("Address not found or does not belong to customer"));

        addressRepository.delete(address);
    }

    @Transactional
    public CustomerAddressDto setDefaultAddress(Integer customerId, Integer addressId) {
        var address = addressRepository.findByAddressIdAndCustomer_CustomerId(addressId, customerId)
            .orElseThrow(() -> new NotFoundException("Address not found or does not belong to customer"));

        // Unset any existing default
        unsetDefaultAddress(customerId);

        // Set this address as default
        address.setIsDefault(true);
        var savedAddress = addressRepository.save(address);

        return addressMapper.toDto(savedAddress);
    }

    private void unsetDefaultAddress(Integer customerId) {
        var currentDefault = addressRepository.findByCustomer_CustomerIdAndIsDefaultTrue(customerId);
        currentDefault.ifPresent(address -> {
            address.setIsDefault(false);
            addressRepository.save(address);
        });
    }
}

