-- IFS-6503 adding table to capture pending partners progress.

CREATE TABLE pending_partner_progress (
  id bigint(20) PRIMARY KEY AUTO_INCREMENT,
  partner_organisation_id bigint(20) NOT NULL UNIQUE,
  your_organisation_completed_on datetime,
  your_funding_completed_on datetime,
  terms_and_conditions_completed_on datetime,
  CONSTRAINT fk_pending_partner_progress_partner_organisation_id FOREIGN KEY (partner_organisation_id) REFERENCES partner_organisation(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;