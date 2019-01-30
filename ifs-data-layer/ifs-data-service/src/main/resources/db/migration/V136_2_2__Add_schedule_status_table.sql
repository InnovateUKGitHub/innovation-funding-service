-- IFS-4494 - Create schedule status table

CREATE TABLE schedule_status (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	version datetime NOT NULL,
	job_name VARCHAR(255) NOT NULL,
	active BOOLEAN NOT NULL DEFAULT FALSE,
	PRIMARY KEY (id),
	UNIQUE KEY job_name_uk (job_name)
);