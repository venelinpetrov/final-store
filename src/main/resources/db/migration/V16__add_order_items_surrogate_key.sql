-- Add order_items surrogate key

ALTER TABLE order_items
	ADD COLUMN order_item_id INT NOT NULL AUTO_INCREMENT FIRST,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (order_item_id),
	ADD UNIQUE KEY idx_order_items_order_variant (order_id, variant_id);

