INSERT INTO activity_state (activity_type, state) VALUES
  ('PROJECT_SETUP_PROJECT_DETAILS', 'PENDING'),
  ('PROJECT_SETUP_PROJECT_DETAILS', 'READY_TO_SUBMIT'),
  ('PROJECT_SETUP_PROJECT_DETAILS', 'SUBMITTED');


INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT p.submitted_date, 'submitted', p.submitted_date, null, 'ProjectDetailsProcess', p.id, pu.id, a.id
    FROM project p
    JOIN project_user pu ON pu.project_id = p.id AND pu.project_role = 'PROJECT_MANAGER'
    JOIN activity_state a ON activity_type = 'PROJECT_SETUP_PROJECT_DETAILS' AND a.state = 'SUBMITTED'
    WHERE p.submitted_date IS NOT NULL;


INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT null, 'start-date-added', now(), null, 'ProjectDetailsProcess', p.id, pu.id, a.id
    FROM project p
    JOIN application app ON app.id = p.application_id
    JOIN process_role pr ON pr.application_id = app.id AND pr.role_id = 1
    JOIN project_user pu ON pu.project_id = p.id AND pu.user_id = pr.user_id and pu.project_role = 'PROJECT_PARTNER'
    JOIN activity_state a ON activity_type = 'PROJECT_SETUP_PROJECT_DETAILS' AND a.state = 'READY_TO_SUBMIT'
    WHERE p.submitted_date IS NULL AND
          p.target_start_date IS NOT NULL AND
          p.address IS NOT NULL AND
          EXISTS (SELECT 1 FROM project_user pu2 WHERE pu2.project_role = 'PROJECT_MANAGER' AND pu2.project_id = p.id);


INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT null, 'pending', now(), null, 'ProjectDetailsProcess', p.id, pu.id, a.id
    FROM project p
    JOIN application app ON app.id = p.application_id
    JOIN process_role pr ON pr.application_id = app.id AND pr.role_id = 1
    JOIN project_user pu ON pu.project_id = p.id AND pu.user_id = pr.user_id and pu.project_role = 'PROJECT_PARTNER'
    JOIN activity_state a ON activity_type = 'PROJECT_SETUP_PROJECT_DETAILS' AND a.state = 'PENDING'
    WHERE NOT EXISTS (SELECT 1 FROM process WHERE process.target_id = p.id AND process.process_type = 'ProjectDetailsProcess');