-- IFS-9961: Assessment as a Service, addition of 2 new columns client application Id, and applicant name.

CREATE TABLE application_external_config (
  application_external_config_id bigint(20) NOT NULL AUTO_INCREMENT,
  external_application_id varchar(255) UNIQUE DEFAULT NULL,
  external_applicant_name varchar(255) DEFAULT NULL,
  PRIMARY KEY (application_external_config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
