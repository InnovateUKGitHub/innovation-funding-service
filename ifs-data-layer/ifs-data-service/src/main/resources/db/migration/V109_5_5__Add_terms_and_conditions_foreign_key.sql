ALTER TABLE competition ADD CONSTRAINT terms_and_conditions_fk FOREIGN KEY (terms_and_conditions_id)
  REFERENCES terms_and_conditions (id);