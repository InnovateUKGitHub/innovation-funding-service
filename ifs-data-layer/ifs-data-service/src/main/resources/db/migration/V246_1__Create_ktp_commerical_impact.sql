CREATE TABLE ktp_commercial_impact_years (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ktp_commercial_impact (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  ktp_commercial_impact_years_id bigint(20) NOT NULL,
  year int NOT NULL,
  inProjectProfit double,
  additionalIncomeStream text,
  total double
) ENGINE=InnoDB DEFAULT CHARSET=utf8;