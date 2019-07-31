-- IFS-4551 Empty form input type

DELETE FROM form_input WHERE form_input_type_id = 6;

-- These form input types are no longer needed.
DELETE FROM form_input_type WHERE id = 6;