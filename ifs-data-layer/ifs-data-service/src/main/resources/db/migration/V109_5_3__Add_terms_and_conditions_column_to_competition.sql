ALTER TABLE competition ADD COLUMN terms_and_conditions_id BIGINT(20) NOT NULL;

ALTER TABLE competition ADD CONSTRAINT terms_and_conditions_fk FOREIGN KEY (terms_and_conditions)
  REFERENCES competition(terms_and_conditions_id);