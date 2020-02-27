-- IFS-6216 Delete unused form inputs

DELETE FROM form_input_response WHERE form_input_id IN (SELECT fi.id FROM form_input fi  WHERE fi.form_input_type_id IN (5, 16, 24, 25, 26, 27, 28));
DELETE FROM form_input_validator WHERE form_input_id IN (SELECT fi.id FROM form_input fi  WHERE fi.form_input_type_id IN (5, 16, 24, 25, 26, 27, 28));
DELETE FROM form_input WHERE form_input_type_id IN (5, 16, 24, 25, 26, 27, 28);

-- These form input types are no longer needed.
DELETE FROM form_input_type WHERE id IN (5, 16, 24, 25, 26, 27, 28);