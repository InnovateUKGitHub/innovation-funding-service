CREATE TABLE competition_external_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  external_competition_id varchar(255) UNIQUE DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;