package com.vpe.finalstore.shipment.security;

import com.vpe.finalstore.shipment.repositories.ShipmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("shipmentSecurity")
public class ShipmentSecurity {
    private final ShipmentRepository shipmentRepository;

    public boolean isOwner(Integer shipmentId, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();

        return shipmentRepository
            .findById(shipmentId)
            .map(shipment -> shipment.getOrder().getCustomer().getUser().getUserId().equals(userId))
            .orElse(false);
    }
}
