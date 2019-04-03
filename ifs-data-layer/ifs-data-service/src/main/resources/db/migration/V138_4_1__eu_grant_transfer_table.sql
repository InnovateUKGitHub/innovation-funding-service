-- IFS-5250

CREATE TABLE eu_action_type (
  id BIGINT(20) PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(255) NOT NULL,
  priority INT(11) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE eu_grant_transfer (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  application_id bigint(20) NOT NULL,
  grant_agreement_number VARCHAR(255),
  participant_id VARCHAR(9),
  eu_action_type_id BIGINT(20),
  project_start_date DATE,
  project_end_date DATE,
  funding_contribution BIGINT(20),
  project_coordinator BOOLEAN DEFAULT FALSE,
  grant_agreement_id bigint(20),
  calculation_spreadsheet_id bigint(20),
  CONSTRAINT fk_eu_grant_transfer_application_id FOREIGN KEY (application_id) REFERENCES application(id),
  CONSTRAINT fk_eu_grant_transfer_action_type_id FOREIGN KEY (eu_action_type_id) REFERENCES eu_action_type(id),
  CONSTRAINT fk_eu_grant_transfer_file_entry_id FOREIGN KEY (grant_agreement_id) REFERENCES file_entry(id),
  CONSTRAINT fk_eu_grant_transfer_calculation_spreadsheet_id FOREIGN KEY (calculation_spreadsheet_id) REFERENCES file_entry(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
