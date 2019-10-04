
CREATE TABLE employees_and_turnover (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  temp_application_finance_id bigint(20),
  temp_project_finance_id bigint(20),
  turnover double,
  employees int(11)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE growth_table (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  temp_application_finance_id bigint(20),
  temp_project_finance_id bigint(20),
  financial_year_end date,
  annual_turnover double,
  annual_profits double,
  annual_export double,
  research_and_development double,
  employees int(11)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
