-- Add Stripe-specific columns to payments table
ALTER TABLE payments
    ADD COLUMN stripe_payment_intent_id VARCHAR(255) NULL COMMENT 'Stripe PaymentIntent ID',
    ADD COLUMN stripe_charge_id         VARCHAR(255) NULL COMMENT 'Stripe Charge ID',
    ADD COLUMN stripe_customer_id       VARCHAR(255) NULL COMMENT 'Stripe Customer ID',
    ADD COLUMN metadata                 TEXT         NULL COMMENT 'Additional Stripe metadata in JSON format';

-- Add indexes for Stripe IDs for faster lookups
CREATE INDEX idx_payments_stripe_payment_intent_id ON payments (stripe_payment_intent_id);
CREATE INDEX idx_payments_stripe_charge_id ON payments (stripe_charge_id);
CREATE INDEX idx_payments_stripe_customer_id ON payments (stripe_customer_id);

-- Create customer_payment_methods table for saved payment methods
CREATE TABLE customer_payment_methods
(
    method_id         INT          NOT NULL AUTO_INCREMENT,
    customer_id       INT          NOT NULL,
    stripe_method_id  VARCHAR(255) NOT NULL COMMENT 'Stripe PaymentMethod ID',
    method_type       VARCHAR(50)  NOT NULL COMMENT 'card, bank_account, etc.',
    card_brand        VARCHAR(50)  NULL COMMENT 'Card brand (visa, mastercard, etc.)',
    last4             VARCHAR(4)   NULL COMMENT 'Last 4 digits of card/account',
    exp_month         INT          NULL COMMENT 'Card expiration month',
    exp_year          INT          NULL COMMENT 'Card expiration year',
    is_default        TINYINT      NOT NULL DEFAULT 0 COMMENT 'Is this the default payment method',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (method_id),
    KEY fk_idx_customer_payment_methods_customer_id (customer_id),
    UNIQUE KEY idx_customer_payment_methods_stripe_pm_id (stripe_method_id),
    FOREIGN KEY (customer_id) REFERENCES customers (customer_id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- Add index for finding default payment method
CREATE INDEX idx_customer_payment_methods_default ON customer_payment_methods (customer_id, is_default);

