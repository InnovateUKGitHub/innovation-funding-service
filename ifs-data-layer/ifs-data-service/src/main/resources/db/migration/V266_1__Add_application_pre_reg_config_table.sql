CREATE TABLE application_pre_reg_config (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  application_id BIGINT(20) NOT NULL,
  pre_registration BIT(1) NOT NULL DEFAULT FALSE,

  CONSTRAINT fk_application_pre_reg_config_application_id FOREIGN KEY (application_id) REFERENCES application(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
