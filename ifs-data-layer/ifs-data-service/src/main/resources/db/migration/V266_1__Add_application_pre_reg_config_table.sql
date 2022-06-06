CREATE TABLE application_pre_reg_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  pre_registration BIT(1) NOT NULL DEFAULT FALSE
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;