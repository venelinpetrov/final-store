-- Add customer_payment_method_id to payments table
ALTER TABLE payments
ADD COLUMN customer_payment_method_id INT NULL COMMENT 'Link to saved customer payment method',
ADD CONSTRAINT fk_payments_customer_payment_method
    FOREIGN KEY (customer_payment_method_id)
    REFERENCES customer_payment_methods(method_id)
    ON DELETE SET NULL;

-- Add index for faster lookups
CREATE INDEX idx_payments_customer_payment_method ON payments(customer_payment_method_id);
