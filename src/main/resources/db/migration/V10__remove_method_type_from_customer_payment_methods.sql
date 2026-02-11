-- Remove redundant method_type column from customer_payment_methods
-- The payment type is already tracked in the payment_methods table via payments.method_id

ALTER TABLE customer_payment_methods
DROP COLUMN method_type;

