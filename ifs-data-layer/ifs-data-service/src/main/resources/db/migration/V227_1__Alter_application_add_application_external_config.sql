ALTER TABLE application ADD COLUMN application_external_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE application ADD UNIQUE KEY  application_external_config_id__UNIQUE (application_external_config_id);
ALTER TABLE application ADD CONSTRAINT fk_applikcation_external_config FOREIGN KEY(application_external_config_id) REFERENCES application_external_config(id);
