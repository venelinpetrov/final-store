-- Ensures that under no circumstances the DB would end up with 2 carts for the same customer
ALTER TABLE carts
    ADD CONSTRAINT uk_carts_customer_id UNIQUE (customer_id);