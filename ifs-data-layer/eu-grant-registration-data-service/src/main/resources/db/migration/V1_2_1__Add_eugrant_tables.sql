-- eu grant registration tables

CREATE TABLE eu_address (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  address_line1 VARCHAR(255),
  address_line2 VARCHAR(255),
  address_line3 VARCHAR(255),
  town VARCHAR(255),
  postcode VARCHAR(255),
  county VARCHAR(255)
);

CREATE TABLE eu_organisation (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  company_house_number VARCHAR(255),
  organisation_type ENUM('BUSINESS', 'RESEARCH', 'RTO', 'PUBLIC_SECTOR_OR_CHARITY') NOT NULL,
  eu_address_id BIGINT(20) NOT NULL,

  CONSTRAINT fk_eu_address_id FOREIGN KEY (eu_address_id) REFERENCES eu_address(id)
);

CREATE TABLE eu_contact (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  email VARCHAR(255),
  telephone VARCHAR(255)
);

CREATE TABLE eu_action_type (
  id BIGINT(20) PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  priority INT(11) NOT NULL UNIQUE
);

CREATE TABLE eu_funding (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,

  eu_action_type_id BIGINT(20) NOT NULL,
  call_name VARCHAR(255) NOT NULL,
  call_id VARCHAR(255) NOT NULL,
  topic_id VARCHAR(255) NOT NULL,
  project_name VARCHAR(255) NOT NULL,

  project_start_date DATE NOT NULL,
  project_end_date DATE NOT NULL,

  grant_agreement_number VARCHAR(255) NOT NULL,
  fundingContribution BIGINT(20) NOT NULL,

  project_coordinator BOOLEAN DEFAULT FALSE NOT NULL,

  CONSTRAINT fk_eu_action_type_id FOREIGN KEY (eu_action_type_id) REFERENCES eu_action_type(id)
);

ALTER TABLE eu_grant ADD COLUMN eu_organisation_id BIGINT(20);
ALTER TABLE eu_grant ADD CONSTRAINT fk_eu_organisation_id FOREIGN KEY (eu_organisation_id) REFERENCES eu_organisation(id);

ALTER TABLE eu_grant ADD COLUMN eu_contact_id BIGINT(20);
ALTER TABLE eu_grant ADD CONSTRAINT fk_eu_contact_id FOREIGN KEY (eu_contact_id) REFERENCES eu_contact(id);

ALTER TABLE eu_grant ADD COLUMN eu_funding_id BIGINT(20);
ALTER TABLE eu_grant ADD CONSTRAINT fk_eu_funding_id FOREIGN KEY (eu_funding_id) REFERENCES eu_funding(id);

ALTER TABLE eu_grant ADD COLUMN submitted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE eu_grant ADD COLUMN short_code VARCHAR(12);
ALTER TABLE eu_grant ADD UNIQUE KEY u_short_code (short_code);