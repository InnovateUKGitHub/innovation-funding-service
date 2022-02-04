-- IFS-11116 / IFS-11202

-- related ticket IFS-10736
ALTER TABLE application_finance
    ADD COLUMN fec_cert_expiry_date DATE NULL DEFAULT NULL AFTER fec_file_entry_id;
ALTER TABLE project_finance
    ADD COLUMN fec_cert_expiry_date DATE NULL DEFAULT NULL AFTER fec_file_entry_id;