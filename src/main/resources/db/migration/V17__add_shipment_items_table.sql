-- Add shipment_items table

CREATE TABLE shipment_items
(
	shipment_item_id INT NOT NULL AUTO_INCREMENT,
	shipment_id INT NOT NULL,
	order_item_id INT NOT NULL,
	quantity INT NOT NULL,
	PRIMARY KEY (shipment_item_id),
	KEY fk_idx_shipment_items_shipment_id (shipment_id),
	KEY fk_idx_shipment_items_order_item_id (order_item_id),
	UNIQUE KEY idx_shipment_items_shipment_order_item (shipment_id, order_item_id),
	CONSTRAINT fk_shipment_items_shipments
		FOREIGN KEY (shipment_id)
		REFERENCES shipments (shipment_id)
		ON DELETE RESTRICT,
	CONSTRAINT fk_shipment_items_order_items
		FOREIGN KEY (order_item_id)
		REFERENCES order_items (order_item_id)
		ON DELETE RESTRICT
);