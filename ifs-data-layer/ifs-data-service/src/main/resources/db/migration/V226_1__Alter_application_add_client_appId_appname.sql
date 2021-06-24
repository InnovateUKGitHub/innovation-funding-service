-- IFS-9961: Assessment as a Service, addition of 2 new columns client application Id, and applicant name.

ALTER TABLE application
ADD COLUMN client_application_id bigint(20) DEFAULT NULL,
ADD COLUMN applicant_name varchar(255) DEFAULT NULL;