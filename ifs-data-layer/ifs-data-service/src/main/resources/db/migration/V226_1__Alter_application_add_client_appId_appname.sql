-- IFS-9961: Assessment as a Service, addition of 2 new columns client application Id, and applicant name.

ALTER TABLE application
ADD COLUMN client_application_id varchar(255) UNIQUE DEFAULT NULL,
ADD COLUMN client_applicant_name varchar(255) DEFAULT NULL;