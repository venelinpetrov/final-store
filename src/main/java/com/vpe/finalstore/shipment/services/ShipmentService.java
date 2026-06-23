package com.vpe.finalstore.shipment.services;

import com.vpe.finalstore.exceptions.NotFoundException;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.shipment.dtos.ShipmentDto;
import com.vpe.finalstore.shipment.entities.Shipment;
import com.vpe.finalstore.shipment.entities.ShipmentItem;
import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import com.vpe.finalstore.shipment.mappers.ShipmentMapper;
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
    private final ShipmentMapper shipmentMapper;

    public ShipmentStatusType getShipmentStatus(Integer shipmentId) {
        return shipmentTrackingEventRepository.getLatestEvent(shipmentId)
            .map(event -> event.getStatus().getName())
            .orElseThrow(() -> new NotFoundException("No tracking events found for shipment"));
    }

    public ShipmentDto getShipmentDetail(Integer shipmentId) {
        var shipment = shipmentRepository.findShipmentWithDetails(shipmentId)
            .orElseThrow(() -> new NotFoundException("Shipment not found"));

        var dto = shipmentMapper.toDto(shipment);
        var status = getShipmentStatus(shipmentId);
        dto.setStatus(status);

        return dto;
    }

    public Shipment getShipmentEntity(Integer shipmentId) {
        return shipmentRepository.findShipmentWithDetails(shipmentId)
            .orElseThrow(() -> new NotFoundException("Shipment not found"));
    }

    @Transactional
    public ShipmentDto createShipment(Integer carrierId, Order order) {
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

        var dto = shipmentMapper.toDto(shipment);
        dto.setStatus(ShipmentStatusType.PENDING);

        return dto;
    }

    private String generateTrackingNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TRK-" + date + "-" + random;
    }

}
