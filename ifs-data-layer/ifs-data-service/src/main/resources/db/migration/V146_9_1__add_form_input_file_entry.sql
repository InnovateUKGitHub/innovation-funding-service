ALTER TABLE form_input ADD COLUMN file_entry_id BIGINT(20);
ALTER TABLE form_input ADD CONSTRAINT fk_form_input_file_entry FOREIGN KEY (file_entry_id) REFERENCES file_entry(id);
ALTER TABLE form_input DROP COLUMN allowed_file_types;
-- check with mark b above

DELETE FROM form_input_type WHERE id in (1, 3, 18);
INSERT INTO form_input_type (id, name) VALUES ('29', 'TEMPLATE_UPLOAD');

-- Appendix shouldn't exist for your organisation
DELETE aft FROM appendix_file_types aft
INNER JOIN form_input fi
ON fi.id = aft.form_input_id
INNER JOIN question q
ON q.id = fi.question_id
INNER JOIN section s
ON q.section_id = s.id
WHERE s.section_type = 'ORGANISATION_FINANCES'
AND fi.form_input_type_id = 4;

DELETE fi FROM form_input fi
INNER JOIN question q
ON q.id = fi.question_id
INNER JOIN section s
ON q.section_id = s.id
WHERE s.section_type = 'ORGANISATION_FINANCES'
AND fi.form_input_type_id = 4;

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
