-- IFS-7194: Domain model updates for non-UK based organisations
ALTER TABLE address ADD COLUMN country VARCHAR(255) DEFAULT NULL;

ALTER TABLE organisation ADD COLUMN international BIT(1) DEFAULT NULL;
ALTER TABLE organisation ADD COLUMN international_registration_number VARCHAR(255) DEFAULT NULL;

INSERT INTO address_type VALUES (5,'INTERNATIONAL');

ALTER TABLE competition ADD COLUMN competition_organisation_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE competition ADD UNIQUE KEY competition_organisation_config_id_UNIQUE (competition_organisation_config_id);
ALTER TABLE competition ADD CONSTRAINT fk_competition_organisation_config FOREIGN KEY(competition_organisation_config_id) REFERENCES competition_organisation_config(id);