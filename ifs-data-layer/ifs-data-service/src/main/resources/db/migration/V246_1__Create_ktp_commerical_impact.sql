CREATE TABLE ktp_commercial_impact_years (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  in_project_profit double,
  additional_income_stream longtext,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ktp_commercial_impact (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  ktp_commercial_impact_years_id bigint(20) NOT NULL,
  year int NOT NULL,
  CONSTRAINT fk_ktp_commercial_impact_to_ktp_commercial_impact_years FOREIGN KEY (ktp_commercial_impact_years_id) REFERENCES ktp_commercial_impact_years(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;