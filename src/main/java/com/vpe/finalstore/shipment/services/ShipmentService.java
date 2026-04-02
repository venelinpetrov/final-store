package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.shipment.entities.Shipment;
import com.vpe.finalstore.shipment.entities.ShipmentItem;
import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import com.vpe.finalstore.shipment.repositories.CarrierRepository;
import com.vpe.finalstore.shipment.repositories.ShipmentRepository;
import com.vpe.finalstore.shipment.repositories.ShipmentStatusRepository;
import com.vpe.finalstore.shipment.repositories.ShipmentTrackingEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final CarrierRepository carrierRepository;
    private final ShipmentTrackingEventService shipmentTrackingEventService;
    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ShipmentTrackingEventRepository shipmentTrackingEventRepository;

    public ShipmentStatusType getShipmentStatus(Integer shipmentId) {
        var latestEvent = shipmentTrackingEventRepository.getLatestEvent(shipmentId);

        return latestEvent.getStatus().getName();
    }

    public Shipment getShipmentDetail(Integer shipmentId) {
        return shipmentRepository.findShipmentWithDetails(shipmentId)
            .orElseThrow(() -> new NotFoundException("Shipment not found"));
    }

    @Transactional
    public Shipment createShipment(Integer carrierId, Order order) {
        var shipment = new Shipment();
        var trackingNumber = generateTrackingNumber();

        shipment.setTrackingNumber(trackingNumber);

        var carrier = carrierRepository.findById(carrierId)
            .orElseThrow(() -> new NotFoundException("Carrier not found"));
        shipment.setCarrier(carrier);

        var address = order.getAddress();
        shipment.setAddress(address);

        shipment.setShipmentDate(LocalDateTime.now());
        shipment.setOrder(order);

        var shipmentItems = new HashSet<ShipmentItem>();
        for (var orderItem : order.getOrderItems()) {
            var shipmentItem = new ShipmentItem();
            shipmentItem.setShipment(shipment);
            shipmentItem.setOrderItem(orderItem);
            shipmentItem.setQuantity(orderItem.getQuantity());
            shipmentItems.add(shipmentItem);
        }

        shipment.setShipmentItems(shipmentItems);
        shipment = shipmentRepository.save(shipment);

        var pendingStatus = shipmentStatusRepository.findByName(ShipmentStatusType.PENDING)
            .orElseThrow(() -> new NotFoundException("Shipment status not found"));

        shipmentTrackingEventService.createEvent(shipment, pendingStatus);

        return shipment;

    }

    private String generateTrackingNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TRK-" + date + "-" + random;
    }

}
