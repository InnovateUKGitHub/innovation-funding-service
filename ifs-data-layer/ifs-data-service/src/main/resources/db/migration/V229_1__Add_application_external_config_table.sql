CREATE TABLE application_external_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  external_application_id varchar(255) UNIQUE DEFAULT NULL,
  external_applicant_name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
