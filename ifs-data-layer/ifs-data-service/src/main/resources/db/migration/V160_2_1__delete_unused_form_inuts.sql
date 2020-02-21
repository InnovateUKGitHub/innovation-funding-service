-- IFS-4551 Empty form input type deletion

delete from form_input_validator where form_input_id in (select fi.id from form_input fi  WHERE fi.form_input_type_id in (5, 16, 24, 25, 26, 27, 28));
DELETE FROM form_input WHERE form_input_type_id in (5, 16, 24, 25, 26, 27, 28);

-- These form input types are no longer needed.
DELETE FROM form_input_type WHERE id in (5, 16, 24, 25, 26, 27, 28);