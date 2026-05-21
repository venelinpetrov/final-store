ALTER TABLE carts
ADD COLUMN coupon_id INT NULL,
ADD CONSTRAINT fk_carts_coupon
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE SET NULL;

CREATE INDEX idx_carts_coupon_id ON carts(coupon_id);
