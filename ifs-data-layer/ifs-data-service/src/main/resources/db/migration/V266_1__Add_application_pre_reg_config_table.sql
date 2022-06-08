CREATE TABLE application_pre_reg_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  pre_registration BIT(1) NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Update application table to have pre-reg-config mapping
ALTER TABLE application ADD COLUMN application_pre_reg_config_id BIGINT(20) DEFAULT NULL;
ALTER TABLE application ADD UNIQUE KEY  application_pre_reg_config_id (application_pre_reg_config_id);
ALTER TABLE application ADD CONSTRAINT fk_application_pre_reg_config FOREIGN KEY(application_pre_reg_config_id) REFERENCES application_pre_reg_config(id);
