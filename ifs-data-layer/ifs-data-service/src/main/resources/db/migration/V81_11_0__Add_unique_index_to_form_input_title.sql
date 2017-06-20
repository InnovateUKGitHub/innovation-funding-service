ALTER TABLE form_input_type MODIFY title VARCHAR(255) NOT NULL;
CREATE UNIQUE INDEX form_input_type_title_uindex ON form_input_type (title);