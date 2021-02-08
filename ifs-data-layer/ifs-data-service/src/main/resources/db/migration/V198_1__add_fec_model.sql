ALTER TABLE application_finance
ADD COLUMN fec_file_entry_id bigint(20) DEFAULT NULL,
ADD COLUMN fec_model_enabled BOOLEAN NULL;

ALTER TABLE project_finance
ADD COLUMN fec_file_entry_id bigint(20) DEFAULT NULL,
ADD COLUMN fec_model_enabled BOOLEAN NULL;