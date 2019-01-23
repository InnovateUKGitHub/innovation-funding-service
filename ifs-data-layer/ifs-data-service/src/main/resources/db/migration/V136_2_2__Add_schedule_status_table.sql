-- IFS-4494 - Create schedule status table

CREATE TABLE schedule_status (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	version datetime NOT NULL,
	job_name VARCHAR(255) NOT NULL,
	active bit(1) NOT NULL DEFAULT b'0',
	PRIMARY KEY (id),
	UNIQUE KEY job_name_uk (job_name)
);