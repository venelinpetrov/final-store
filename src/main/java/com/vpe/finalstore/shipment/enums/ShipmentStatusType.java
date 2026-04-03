package com.vpe.finalstore.shipment.enums;

public enum ShipmentStatusType {
	PENDING, // Shipment is being prepared
	SHIPPED, // Left the warehouse
	IN_TRANSIT, // Moving through the carrier network
	DELIVERED, // Successfully delivered
	CANCELED, // Order was canceled before shipping
	RETURNED, // Sent back to seller
	FAILED // Delivery attempt unsuccessful
}
