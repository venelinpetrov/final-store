package com.vpe.finalstore.shipment.controllers;

import com.vpe.finalstore.shipment.dtos.CarrierCreateDto;
import com.vpe.finalstore.shipment.dtos.CarrierDto;
import com.vpe.finalstore.shipment.mappers.CarrierMapper;
import com.vpe.finalstore.shipment.services.CarrierService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/api/carriers")
public class CarrierController {
    private final CarrierService carrierService;
    private final CarrierMapper carrierMapper;

    @Operation(
        summary = "Get all carriers"
    )
    @GetMapping
    public ResponseEntity<List<CarrierDto>> getAllCarriers() {
        var carriers = carrierService.findAll();

        return ResponseEntity.ok(carrierMapper.toDto(carriers));
    }

    @PostMapping
    public ResponseEntity<CarrierDto> createCarrier(@RequestBody CarrierCreateDto dto) {
        var carrier = carrierService.create(dto);

        return ResponseEntity.ok(carrierMapper.toDto(carrier));
    }

}
