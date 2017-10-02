-- add constraints to finance_row to prevent duplicates
ALTER TABLE finance_row
  ADD CONSTRAINT row_type_application_row_unique UNIQUE (row_type, application_row_id),
  ADD CONSTRAINT finance_row_application_row_id_fk FOREIGN KEY (application_row_id) REFERENCES finance_row(id),
  ADD INDEX finance_row_application_row_id_idx (id);