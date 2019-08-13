-- IFS-5816 Add terms and conditions section and question to existing, unopened, competitions

-- New terms and conditions section
INSERT INTO section
(display_in_assessment_application_summary, name, priority, competition_id, question_group, section_type, description)
SELECT false                                                            AS display_in_assessment_application_summary,
       'Terms and conditions'                                           AS name,
       '11'                                                             AS priority,
       c.id                                                             AS competition_id,
       false                                                            AS question_group,
       'TERMS_AND_CONDITIONS'                                           AS section_type,
       'You must agree to these before you submit your application.'    AS description
FROM competition c;

-- Add the terms question to the terms section
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name,
                      priority, question_number, competition_id, section_id, question_type, question_setup_type)
SELECT false                        AS assign_enabled,
       'Award terms and conditions' AS description,
       true                         AS mark_as_completed_enabled,
       true                         AS multiple_statuses,
       'Award terms and conditions' AS name,
       'Award terms and conditions' AS short_name,
       '1'                          AS priority,
       NULL                         AS question_number,
       c.id                         AS competition_id,
       s.id                         AS section_id,
       'GENERAL'                    AS question_type,
       'TERMS_AND_CONDITIONS'       AS question_setup_type
FROM competition c
INNER JOIN section s ON s.competition_id = c.id AND s.section_type = 'TERMS_AND_CONDITIONS';

-- add a question_status per application/organisation, for all submitted applications
INSERT INTO question_status (application_id, marked_as_complete, marked_as_complete_by_id, marked_as_complete_on,
                             question_id)
SELECT pr.application_id AS application_id,
       true              AS marked_as_complete,
       pr.id             AS marked_as_complete_by_id,
       null              AS marked_as_complete_on, -- null timestamp for existing applications
       q.id              AS question_id
FROM process_role pr
         INNER JOIN application a ON pr.application_id = a.id
         INNER JOIN question q ON q.competition_id = a.competition
         INNER JOIN section s ON q.section_id = s.id
WHERE a.submitted_date IS NOT NULL
  AND s.section_type = 'TERMS_AND_CONDITIONS'
GROUP BY pr.application_id, pr.organisation_id;