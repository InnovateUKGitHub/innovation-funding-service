CREATE TABLE competition_organisation_config (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  international_organisations_allowed BIT(1) DEFAULT FALSE,
  international_lead_organisation_allowed BIT(1) DEFAULT FALSE,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;