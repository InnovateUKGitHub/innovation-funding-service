ALTER TABLE form_input_type CHANGE title name VARCHAR(255) NOT NULL;
UPDATE form_input_type SET name = UPPER(name);