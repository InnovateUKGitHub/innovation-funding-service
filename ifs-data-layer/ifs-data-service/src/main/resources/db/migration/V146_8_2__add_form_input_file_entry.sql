-- IFS-6186 Template form input type

-- Add new form input field for the template document
ALTER TABLE form_input ADD COLUMN file_entry_id BIGINT(20);
ALTER TABLE form_input ADD CONSTRAINT fk_form_input_file_entry FOREIGN KEY (file_entry_id) REFERENCES file_entry(id);

-- This column is no longer used.
ALTER TABLE form_input DROP COLUMN allowed_file_types;
-- check with mark b above

-- Insert new form input type for template document.
INSERT INTO form_input_type (id, name) VALUES ('29', 'TEMPLATE_DOCUMENT');

-- Insert inactive template document form inputs wherever there is an appendix form input.
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)

SELECT NULL                 as word_count,
       29                   as form_input_type_id,
       fi.competition_id    as competition_id,
       0                    as included_in_application_summary,
       'Template'           as description,
       NULL                 as guidance_title,
       NULL                 as guidance_answer,
       fi.priority + 1      as priority,
       fi.question_id       as question_id,
       'APPLICATION'        as scope,
       0                    as active
FROM form_input fi
WHERE fi.form_input_type_id = 4;
