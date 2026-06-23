package com.vpe.finalstore.shipment.controllers;

import com.vpe.finalstore.shipment.dtos.CarrierCreateDto;
import com.vpe.finalstore.shipment.dtos.CarrierDto;
import com.vpe.finalstore.shipment.dtos.CarrierUpdateDto;
import com.vpe.finalstore.shipment.services.CarrierService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/api/carriers")
public class CarrierController {
    private final CarrierService carrierService;

    @Operation(
        summary = "Get all carriers"
    )
    @GetMapping
    public ResponseEntity<List<CarrierDto>> getAllCarriers() {
        return ResponseEntity.ok(carrierService.findAll());
    }

    @Operation(
        summary = "Create a new carrier"
    )
    @PostMapping
    public ResponseEntity<CarrierDto> createCarrier(@RequestBody CarrierCreateDto dto) {
        return ResponseEntity.ok(carrierService.create(dto));
    }

    @Operation(
        summary = "Update a carrier"
    )
    @PutMapping("/{carrierId}")
    public ResponseEntity<CarrierDto> updateCarrier(@PathVariable Integer carrierId, @RequestBody CarrierUpdateDto dto) {
        return ResponseEntity.ok(carrierService.update(carrierId, dto));
    }

}
