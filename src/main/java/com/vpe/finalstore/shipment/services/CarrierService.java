package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.shipment.dtos.CarrierCreateDto;
import com.vpe.finalstore.shipment.dtos.CarrierDto;
import com.vpe.finalstore.shipment.dtos.CarrierUpdateDto;
import com.vpe.finalstore.shipment.mappers.CarrierMapper;
import com.vpe.finalstore.shipment.repositories.CarrierRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CarrierService {
    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    public List<CarrierDto> findAll() {
        var carriers = carrierRepository.findAll();
        return carrierMapper.toDto(carriers);
    }

    public CarrierDto create(CarrierCreateDto dto) {
        var carrier = carrierRepository.save(carrierMapper.toEntity(dto));
        return carrierMapper.toDto(carrier);
    }

    public CarrierDto update(Integer carrierId, CarrierUpdateDto dto) {
        var carrier = carrierRepository.findById(carrierId)
            .orElseThrow(() -> new NotFoundException("Carrier not found"));

        carrier.setName(dto.getName());
        carrier.setCode(dto.getCode());
        carrier.setTrackingUrlTemplate(dto.getTrackingUrlTemplate());
        carrier.setApiEndpoint(dto.getApiEndpoint());

        var updatedCarrier = carrierRepository.save(carrier);
        return carrierMapper.toDto(updatedCarrier);
    }
}
