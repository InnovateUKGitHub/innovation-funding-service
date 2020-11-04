ALTER TABLE schedule_status DROP COLUMN version;
ALTER TABLE schedule_status DROP COLUMN active;
DELETE FROM schedule_status;
ALTER TABLE schedule_status ADD COLUMN created_on DATETIME NOT NULL;

CREATE TABLE project_to_be_created (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	application_id bigint(20) NOT NULL,
	created datetime NOT NULL,
	last_modified datetime DEFAULT NULL,
	pending BOOLEAN DEFAULT TRUE NOT NULL,
	message VARCHAR(255),
	email_body longtext,
	PRIMARY KEY (id),
	UNIQUE KEY application_id_uk (application_id),
	CONSTRAINT project_to_be_created_application_fk FOREIGN KEY (application_id) REFERENCES application (id)
);