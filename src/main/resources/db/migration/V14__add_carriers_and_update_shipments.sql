-- Create carriers table
CREATE TABLE carriers
(
    carrier_id             INT           NOT NULL AUTO_INCREMENT,
    name                   VARCHAR(100)  NOT NULL,
    code                   VARCHAR(100)  NOT NULL,
    tracking_url_template  VARCHAR(500)  DEFAULT NULL,
    api_endpoint           VARCHAR(500)  DEFAULT NULL,
    PRIMARY KEY (carrier_id),
    UNIQUE KEY idx_carriers_name_UNIQUE (name),
    UNIQUE KEY idx_carriers_code_UNIQUE (code)
) ENGINE = InnoDB;

-- Alter shipments table to use carrier_id instead of carrier string
ALTER TABLE shipments
    ADD COLUMN carrier_id INT NULL AFTER shipment_id,
    ADD KEY fk_idx_shipments_carrier_id (carrier_id),
    ADD CONSTRAINT fk_shipments_carriers FOREIGN KEY (carrier_id) REFERENCES carriers (carrier_id);

-- Drop the old carrier column (after adding carrier_id)
ALTER TABLE shipments
    DROP COLUMN carrier;

