ALTER TABLE competition ADD COLUMN competition_external_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE competition ADD UNIQUE KEY  competition_external_config_id__UNIQUE (competition_external_config_id);
ALTER TABLE competition ADD CONSTRAINT fk_competition_external_config FOREIGN KEY(competition_external_config_id) REFERENCES competition_external_config(id);
