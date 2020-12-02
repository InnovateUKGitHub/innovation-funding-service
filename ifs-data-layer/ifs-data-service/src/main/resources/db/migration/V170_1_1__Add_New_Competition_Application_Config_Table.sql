CREATE TABLE competition_application_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  temporary_competition_id bigint(20) NOT NULL,
  maximum_funding_sought double DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
