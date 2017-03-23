ALTER TABLE spend_profile
ADD CONSTRAINT UC_project_org UNIQUE (project_id, organisation_id);