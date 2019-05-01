-- Add terms and conditions section and question to existing, unopened, competitions

-- New terms and conditions section
INSERT INTO section
(display_in_assessment_application_summary, name, priority, competition_id, question_group, section_type)
SELECT false                  AS display_in_assessment_application_summary,
       'Terms and conditions' AS name,
       '1'                    AS priority,
       c.id                   AS competition_id,
       false                  AS question_group,
       'TERMS_AND_CONDITIONS' AS section_type
FROM competition c
         INNER JOIN milestone m on c.id = m.competition_id
WHERE setup_complete
  AND m.type = 'OPEN_DATE'
  AND m.date > now();

-- Add the terms question to the terms section
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name,
                      priority, question_number, competition_id, section_id, question_type, question_setup_type)
SELECT false                  AS assign_enabled,
       'Terms and conditions' AS description,
       true                   AS mark_as_completed_enabled,
       false                  AS multiple_statuses,
       'Terms and Conditions' AS name,
       'T&C'                  AS short_name,
       '11'                   AS priority,
       '11'                   AS question_number,
       c.id                   AS competition_id,
       s.id                   AS section_id,
       'GENERAL'              AS question_type,
       'TERMS_AND_CONDITIONS' AS question_setup_type
FROM competition c
         INNER JOIN section s ON s.competition_id = c.id AND s.section_type = 'TERMS_AND_CONDITIONS'
         INNER JOIN milestone m on c.id = m.competition_id
WHERE setup_complete
  AND m.type = 'OPEN_DATE'
  AND m.date > now();