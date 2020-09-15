-- Insert inactive multiple choice for template comp wherever there is an textarea
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)

SELECT NULL                 as word_count,
       30                   as form_input_type_id,
       fi.competition_id    as competition_id,
       0                    as included_in_application_summary,
       'Multiple choice'    as description,
       NULL                 as guidance_title,
       NULL                 as guidance_answer,
       3                    as priority,
       fi.question_id       as question_id,
       'APPLICATION'        as scope,
       0                    as active
FROM form_input fi
INNER JOIN competition c on c.id = fi.competition_id
WHERE fi.form_input_type_id = 2
AND c.template = 1
AND fi.scope = 'APPLICATION';

-- insert validators for multiple choice.
INSERT INTO form_input_validator (form_input_id, form_validator_id)

SELECT fi.id as form_input_id,
       11    as form_validator_id
FROM form_input fi
WHERE fi.form_input_type_id = 30;