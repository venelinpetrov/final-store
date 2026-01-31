package com.vpe.finalstore.customer.controllers;

import com.vpe.finalstore.customer.dtos.CustomerAddressCreateDto;
import com.vpe.finalstore.customer.dtos.CustomerAddressDto;
import com.vpe.finalstore.customer.dtos.CustomerAddressUpdateDto;
import com.vpe.finalstore.customer.services.CustomerAddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
class CustomerAddressController {
    private final CustomerAddressService addressService;

    @Operation(
        summary = "Get all addresses for a customer"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @GetMapping
    public ResponseEntity<List<CustomerAddressDto>> getAddresses(@PathVariable Integer customerId) {
        var addresses = addressService.getAddressesByCustomerId(customerId);
        return ResponseEntity.ok(addresses);
    }

    @Operation(
        summary = "Get a specific address"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @GetMapping("/{addressId}")
    public ResponseEntity<CustomerAddressDto> getAddress(
        @PathVariable Integer customerId,
        @PathVariable Integer addressId
    ) {
        var address = addressService.getAddressById(customerId, addressId);
        return ResponseEntity.ok(address);
    }

    @Operation(
        summary = "Create a new address"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @PostMapping
    public ResponseEntity<CustomerAddressDto> createAddress(
        @PathVariable Integer customerId,
        @Valid @RequestBody CustomerAddressCreateDto dto,
        UriComponentsBuilder uriBuilder
    ) {
        var address = addressService.createAddress(customerId, dto);
        var uri = uriBuilder
            .path("/api/customers/{customerId}/addresses/{addressId}")
            .buildAndExpand(customerId, address.getAddressId())
            .toUri();

        return ResponseEntity.created(uri).body(address);
    }

    @Operation(
        summary = "Update an address"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @PutMapping("/{addressId}")
    public ResponseEntity<CustomerAddressDto> updateAddress(
        @PathVariable Integer customerId,
        @PathVariable Integer addressId,
        @Valid @RequestBody CustomerAddressUpdateDto dto
    ) {
        var address = addressService.updateAddress(customerId, addressId, dto);
        return ResponseEntity.ok(address);
    }

    @Operation(
        summary = "Delete an address"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
        @PathVariable Integer customerId,
        @PathVariable Integer addressId
    ) {
        addressService.deleteAddress(customerId, addressId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Set an address as default"
    )
    @PreAuthorize("@customerSecurity.isOwner(#customerId, authentication)")
    @PatchMapping("/{addressId}/set-default")
    public ResponseEntity<CustomerAddressDto> setDefaultAddress(
        @PathVariable Integer customerId,
        @PathVariable Integer addressId
    ) {
        var address = addressService.setDefaultAddress(customerId, addressId);
        return ResponseEntity.ok(address);
    }
}

