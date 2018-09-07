-- IFS-3950 - removing redundant form_input_response rows before enforcing non-null foreign key constraints
-- UPDATE: There are issues with adding NOT NULL constraints to application id in V129_3_2__Add_form_input_non_null_foreign_key_to_form_input.sql
-- Those NOT NULL constraints have been reverted in V129_5_1. As a result the below statement has been commented (as this has not yet been run on production)

--DELETE FROM form_input_response WHERE application_id IS NULL OR form_input_id IS NULL;