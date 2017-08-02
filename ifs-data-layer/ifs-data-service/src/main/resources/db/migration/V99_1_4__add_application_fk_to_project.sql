ALTER TABLE project 
ADD INDEX project_application_fk_idx (application_id ASC);

ALTER TABLE project 
ADD CONSTRAINT project_application_fk
  FOREIGN KEY (application_id)
  REFERENCES application (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;