
CREATE TABLE ktp_financial_years (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  group_employees int,
  financial_year_end date
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ktp_financial_year (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  ktp_financial_years_id bigint(20) NOT NULL,
  year int NOT NULL,
  turnover double,
  pre_tax_profit double,
  current_assets double,
  liabilities double,
  shareholder_value double,
  loans double,
  employees int,
  CONSTRAINT fk_ktp_financial_year_to_ktp_financial_years FOREIGN KEY (ktp_financial_years_id) REFERENCES ktp_financial_years(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE application_finance
    ADD COLUMN ktp_financial_years_id bigint(20),
    ADD CONSTRAINT fk_application_finance_ktp_financial_years_id FOREIGN KEY (ktp_financial_years_id) REFERENCES ktp_financial_years (id);

ALTER TABLE project_finance
    ADD COLUMN ktp_financial_years_id bigint(20),
    ADD CONSTRAINT fk_project_finance_ktp_financial_years_id FOREIGN KEY (ktp_financial_years_id) REFERENCES ktp_financial_years (id);
