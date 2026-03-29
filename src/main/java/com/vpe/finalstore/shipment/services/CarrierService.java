package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.shipment.dtos.CarrierCreateDto;
import com.vpe.finalstore.shipment.entities.Carrier;
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

    public List<Carrier> findAll() {
        return carrierRepository.findAll();
    }

    public Carrier create(CarrierCreateDto carrier) {
        return carrierRepository.save(carrierMapper.toEntity(carrier));
    }
}
