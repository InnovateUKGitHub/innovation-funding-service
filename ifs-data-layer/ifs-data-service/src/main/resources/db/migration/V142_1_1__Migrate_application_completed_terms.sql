INSERT INTO section
(display_in_assessment_application_summary, name, competition_id, question_group, section_type)
SELECT false AS display_in_assessment_application_summary,
'Terms and conditions' AS name,
c.id AS competition_id,
false AS question_group,
'GENERAL' AS section_type
FROM competition c
WHERE setup_complete = 1;

INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_number, competition_id, section_id, question_type)
SELECT false AS assign_enabled,
'Terms and conditions' AS description,
false AS mark_as_completed_enabled,
false AS multiple_statuses,
'Terms and Conditions' AS name,
'T&C' AS short_name,
'11' AS priority,
'11' AS question_number,
c.id AS competition_id,
s.id AS section_id,
'GENERAL' AS question_type
FROM competition c
INNER JOIN section s ON
s.competition_id = c.id AND s.name = 'Terms and conditions'
WHERE setup_complete = 1;

-- insert complete status for each organisation on an application that is already complete, leave blank for applications in flight
INSERT INTO question_status
(application_id, question_id)
SELECT a.id AS application_id,
q.id AS question_id
FROM application a
INNER JOIN question q
ON q.competition_id = a.competition
WHERE submitted_date IS NOT NUll;

