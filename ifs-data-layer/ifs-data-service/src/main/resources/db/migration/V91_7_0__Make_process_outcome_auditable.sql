ALTER TABLE process_outcome ADD created_on DATETIME;
ALTER TABLE process_outcome ADD modified_by BIGINT(20);
ALTER TABLE process_outcome ADD modified_on DATETIME;
ALTER TABLE process_outcome ADD created_by BIGINT(20);

ALTER TABLE process_outcome ADD CONSTRAINT process_outcome_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES user (id);

ALTER TABLE process_outcome ADD CONSTRAINT process_outcome_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES user (id);