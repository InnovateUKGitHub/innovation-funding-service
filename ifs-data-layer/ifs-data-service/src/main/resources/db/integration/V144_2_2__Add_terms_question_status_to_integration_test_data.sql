-- IFS-5667 - add accepted terms and conditions to the ready-to-submit application (7)

INSERT INTO question_status (application_id, marked_as_complete, marked_as_complete_by_id, marked_as_complete_on,
                             question_id)
SELECT a.id              AS application_id,
       true              AS marked_as_complete,
       pr.id             AS marked_as_complete_by_id,
       null              AS marked_as_complete_on,
       q.id              AS question_id
FROM process_role pr
         INNER JOIN application a ON pr.application_id = a.id
         INNER JOIN question q ON q.competition_id = a.competition
         INNER JOIN section s ON q.section_id = s.id
WHERE a.id = 7
  AND s.section_type = 'TERMS_AND_CONDITIONS'
GROUP BY pr.organisation_id;