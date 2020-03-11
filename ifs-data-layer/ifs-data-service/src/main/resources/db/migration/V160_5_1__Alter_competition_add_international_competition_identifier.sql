-- IFS-7194: Domain model updates for non-UK based organisations
ALTER TABLE address ADD COLUMN country VARCHAR(255) DEFAULT NULL;

ALTER TABLE organisation ADD COLUMN is_international BIT(1) NOT NULL DEFAULT FALSE;
ALTER TABLE organisation ADD COLUMN international_registration_number BIT(1) DEFAULT NULL;
