ALTER TABLE project ADD CONSTRAINT UK_one_project_per_application UNIQUE (application_id);
