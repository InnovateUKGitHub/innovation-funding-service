INSERT INTO activity_state (activity_type, state) VALUES
('PROJECT_SETUP', 'PENDING'),
('PROJECT_SETUP', 'ACCEPTED');

INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT null, 'project-created', NOW(), NOW(), 'ProjectProcess', p.id, pu.id, a.id
    FROM project p
    JOIN application app ON app.id = p.application_id
    JOIN process_role pr ON pr.application_id = app.id AND pr.role_id = 1
    JOIN project_user pu ON pu.project_id = p.id AND pu.user_id = pr.user_id and pu.project_role = 'PROJECT_PARTNER'
    JOIN activity_state a ON activity_type = 'PROJECT_SETUP' AND a.state = 'PENDING'
  WHERE NOT EXISTS (SELECT 1 FROM process WHERE process.target_id = p.id AND process.process_type = 'ProjectProcess');