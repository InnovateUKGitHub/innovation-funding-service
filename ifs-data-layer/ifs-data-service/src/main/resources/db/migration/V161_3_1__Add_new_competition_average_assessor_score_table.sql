CREATE TABLE competition_average_assessor_score_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  temporary_competition_id bigint(20) NOT NULL,
  average_assessor_score BIT(1) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY competition_id_UNIQUE (temporary_competition_id),
  CONSTRAINT competition_average_assessor_score_config_fk FOREIGN KEY (temporary_competition_id) REFERENCES competition (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;