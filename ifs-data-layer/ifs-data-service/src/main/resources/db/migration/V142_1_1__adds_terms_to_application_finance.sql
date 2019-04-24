-- IFS-5668 terms and conditions accepted flag and audit fields

ALTER TABLE application_finance ADD COLUMN terms_and_conditions_accepted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE application_finance ADD COLUMN terms_and_conditions_accepted_by BIGINT(20);
ALTER TABLE application_finance ADD COLUMN terms_and_conditions_accepted_on DATETIME;

ALTER TABLE application_finance ADD CONSTRAINT terms_and_conditions_accepted_by_fk
    FOREIGN KEY (terms_and_conditions_accepted_by) REFERENCES user (id);
