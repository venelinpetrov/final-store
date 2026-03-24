-- Create shipment_statuses table
CREATE TABLE shipment_statuses
(
    status_id INT         NOT NULL AUTO_INCREMENT,
    name      VARCHAR(50) NOT NULL,
    PRIMARY KEY (status_id),
    UNIQUE KEY idx_shipment_statuses_name_UNIQUE (name)
) ENGINE = InnoDB;

-- Create shipment_tracking_events table
CREATE TABLE shipment_tracking_events
(
    event_id    INT          NOT NULL AUTO_INCREMENT,
    shipment_id INT          NOT NULL,
    status_id   INT          NOT NULL,
    event_date  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location    VARCHAR(255) DEFAULT NULL,
    description TEXT         DEFAULT NULL,
    PRIMARY KEY (event_id),
    KEY fk_idx_shipment_tracking_events_shipment_id (shipment_id),
    KEY fk_idx_shipment_tracking_events_status_id (status_id),
    CONSTRAINT fk_shipment_tracking_events_shipments FOREIGN KEY (shipment_id) REFERENCES shipments (shipment_id) ON DELETE RESTRICT,
    CONSTRAINT fk_shipment_tracking_events_statuses FOREIGN KEY (status_id) REFERENCES shipment_statuses (status_id)
) ENGINE = InnoDB;

