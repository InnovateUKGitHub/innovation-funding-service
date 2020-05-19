-- Add a date to the organisation_address table to see what the latest date is.

ALTER TABLE organisation_address
  ADD COLUMN created_on DATETIME;

UPDATE organisation_address set created_on = now();

ALTER TABLE organisation_address MODIFY COLUMN created_on DATETIME NOT NULL;

-- New table to link organisation addresses with applications.
CREATE TABLE organisation_application_address (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  organisation_address_id bigint(20) NOT NULL,
  application_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT organisation_application_address_to_application_fk FOREIGN KEY (application_id) REFERENCES application (id),
  CONSTRAINT organisation_application_address_to_organisation_address_fk FOREIGN KEY (organisation_address_id) REFERENCES organisation_address (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;