package com.vpe.finalstore.shipment.enums;

public enum ShipmentStatusType {
	PENDING, // Shipment is being prepared
	DISPATCHED, // Left the warehouse
	IN_TRANSIT, // Moving through the carrier network
	DELIVERED, // Successfully delivered
	CANCELLED, // Order was cancelled before shipping
	RETURNED, // Sent back to seller
	FAILED // Delivery attempt unsuccessful
}
