ALTER TABLE competition ADD COLUMN competition_application_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE competition ADD UNIQUE KEY competition_application_config_id_UNIQUE (competition_application_config_id);
ALTER TABLE competition ADD CONSTRAINT fk_competition_application_config FOREIGN KEY(competition_application_config_id) REFERENCES competition_application_config(id);
