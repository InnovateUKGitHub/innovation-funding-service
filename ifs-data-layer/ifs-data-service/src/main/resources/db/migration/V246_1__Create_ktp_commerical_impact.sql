CREATE TABLE ktp_commercial_impact_years (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ktp_commercial_impact (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  ktp_commercial_impact_years_id bigint(20) NOT NULL,
  year int NOT NULL,
  in_project_profit double,
  additional_income_stream longtext,
  total double,
  CONSTRAINT fk_ktp_commercial_impact_to_ktp_commercial_impact_years FOREIGN KEY (ktp_commercial_impact_years_id) REFERENCES ktp_commercial_impact_years(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;