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

CREATE TABLE eu_action_type (
  id BIGINT(20) PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  priority INT(11) NOT NULL UNIQUE
);
CREATE TABLE eu_organisation (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  company_house_number VARCHAR(255),
  organisation_type ENUM('BUSINESS', 'RESEARCH', 'RTO', 'PUBLIC_SECTOR_OR_CHARITY') NOT NULL
);

CREATE TABLE eu_contact (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  email VARCHAR(255),
  telephone VARCHAR(255)
);

CREATE TABLE eu_funding (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,

  call_name VARCHAR(255),
  call_id VARCHAR(255),
  topic_id VARCHAR(255),
  project_name VARCHAR(255),

  project_start_date DATE,
  project_end_date DATE,

  grant_agreement_number VARCHAR(255),
  fundingContribution BIGINT(20),

  project_coordinator BOOLEAN DEFAULT FALSE
);

ALTER TABLE eu_grant ADD COLUMN eu_organisation_id BIGINT(20);
ALTER TABLE eu_grant ADD COLUMN eu_contact_id BIGINT(20);
ALTER TABLE eu_grant ADD COLUMN eu_funding_id BIGINT(20);
ALTER TABLE eu_grant ADD COLUMN submitted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE eu_grant ADD COLUMN short_code VARCHAR(12); -- UNIQUE KEY;

-- TODO foreign keys