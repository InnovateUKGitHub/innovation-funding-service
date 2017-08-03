ALTER TABLE project
ADD CONSTRAINT project_application_fk
  FOREIGN KEY (application_id)
  REFERENCES application (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;