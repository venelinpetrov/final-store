CREATE TABLE discounts (
	discount_id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	discount_type ENUM('PERCENTAGE', 'FIXED', 'BUY_X_GET_Y') NOT NULL,
	scope ENUM('VARIANT', 'ORDER', 'SHIPPING') NOT NULL,
	value DECIMAL(10, 2) NOT NULL,
	min_order_amount DECIMAL(10, 2),
	max_discount_amount DECIMAL(10, 2),
	valid_from DATETIME,
	valid_until DATETIME,
	is_active BOOLEAN DEFAULT TRUE,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_discounts_active_dates (is_active, valid_from, valid_until)
);

CREATE TABLE discount_conditions (
	condition_id INT AUTO_INCREMENT PRIMARY KEY,
	discount_id INT NOT NULL,
	condition_type ENUM(
		'MIN_QUANTITY',
		'CUSTOMER_GROUP',
		'VARIANT'
	) NOT NULL,
	decimal_value DECIMAL(10, 2),
	int_value INT,
	string_value VARCHAR(255),
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_discount_conditions_discount_id (discount_id),
	FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE RESTRICT
);

CREATE TABLE coupons (
	coupon_id INT AUTO_INCREMENT PRIMARY KEY,
	code VARCHAR(50) NOT NULL UNIQUE,
	discount_id INT NOT NULL,
	usage_limit INT,
	times_used INT DEFAULT 0,
	is_active BOOLEAN DEFAULT TRUE,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_coupons_discount_id (discount_id),
	FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE RESTRICT
);

CREATE TABLE applied_discounts (
	applied_discount_id INT AUTO_INCREMENT PRIMARY KEY,
	order_id INT NOT NULL,
	order_item_id INT NULL,
	discount_id INT NOT NULL,
	coupon_id INT NULL,
	discount_amount DECIMAL(10, 2) NOT NULL,
	applied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_applied_discounts_order_id (order_id),
	INDEX idx_applied_discounts_discount_id (discount_id),
	INDEX idx_applied_discounts_coupon_id (coupon_id),
	FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE RESTRICT,
	FOREIGN KEY (order_item_id) REFERENCES order_items(order_item_id) ON DELETE RESTRICT,
	FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE RESTRICT,
	FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE RESTRICT
);