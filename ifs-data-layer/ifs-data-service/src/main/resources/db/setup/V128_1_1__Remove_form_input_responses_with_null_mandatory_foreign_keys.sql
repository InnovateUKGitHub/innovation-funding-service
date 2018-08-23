-- IFS-3950 - removing redundant form_input_response rows before enforcing non-null foreign key constraints

DELETE FROM form_input_response WHERE application_id IS NULL OR form_input_id IS NULL;