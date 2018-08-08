-- Note: Not bumping the priority of all the existing questions in the Project details section to to make space for the
-- Application team question as it appears a sufficient gap for priority=1 in the sequence already exists

-- Create an Application Team question in the Project details section for all competitions that don't have one
INSERT INTO question (competition_id, section_id, assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_number, assessor_maximum_score, question_type, question_setup_type)
SELECT c.id, s.id, false, 'Description not used', true, false, 'Application team', 'Application team', 1, null, null, 'LEAD_ONLY', 'APPLICATION_TEAM'
FROM
  competition c
  INNER JOIN section s on c.id = s.competition_id AND s.section_type = 'GENERAL' AND s.name = 'Project details'
  LEFT JOIN question q ON c.id = q.competition_id AND q.question_setup_type = 'APPLICATION_TEAM'
WHERE q.id IS NULL;

-- Create a question status for all submitted applications that don't have one for the Application Team question
SET @lead_application_role_id = (SELECT id FROM role WHERE name = 'leadapplicant');

INSERT INTO question_status (application_id, question_id, marked_as_complete_by_id, marked_as_complete)
  SELECT a.id, q.id, pr.id, true FROM application a
    INNER JOIN competition c ON a.competition = c.id
    INNER JOIN question q ON c.id = q.competition_id
    INNER JOIN process_role pr ON a.id = pr.application_id
    INNER JOIN process p ON p.target_id = a.id
    INNER JOIN activity_state ac ON p.activity_state_id = ac.id
    LEFT JOIN question_status qs ON qs.question_id = q.id AND qs.application_id = a.id
  WHERE q.question_setup_type = 'APPLICATION_TEAM'
        AND pr.role_id = @lead_application_role_id
        AND p.process_type = 'ApplicationProcess'
        AND ac.state IN ('SUBMITTED', 'NOT_APPLICABLE', 'NOT_APPLICABLE_INFORMED', 'ACCEPTED', 'REJECTED')
        AND qs.id IS NULL;