CREATE TABLE ktp_commercial_impact_years (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  in_project_profit double,
  additional_income_stream longtext,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ktp_commercial_impact (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  ktp_commercial_impact_years_id bigint(20) NOT NULL,
  year int NOT NULL,
  CONSTRAINT fk_ktp_commercial_impact_to_ktp_commercial_impact_years FOREIGN KEY (ktp_commercial_impact_years_id) REFERENCES ktp_commercial_impact_years(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE application_finance
    ADD COLUMN ktp_commercial_impact_years_id bigint(20),
    ADD CONSTRAINT fk_application_finance_ktp_commercial_impact_years_id FOREIGN KEY (ktp_commercial_impact_years_id) REFERENCES ktp_commercial_impact_years (id);

ALTER TABLE project_finance
    ADD COLUMN ktp_commercial_impact_years_id bigint(20),
    ADD CONSTRAINT fk_project_finance_ktp_commercial_impact_years_id FOREIGN KEY (ktp_commercial_impact_years_id) REFERENCES ktp_commercial_impact_years (id);
