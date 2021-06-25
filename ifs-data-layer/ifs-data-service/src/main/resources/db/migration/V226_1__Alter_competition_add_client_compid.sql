-- IFS-9961: Assessment as a Service, addition of a columnn client competition id

ALTER TABLE competition
ADD COLUMN client_competition_id varchar(255) UNIQUE DEFAULT NULL;