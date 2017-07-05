INSERT INTO activity_state (activity_type, state) VALUES
  ('PROJECT_SETUP_FINANCE_CHECKS', 'PENDING'),
  ('PROJECT_SETUP_FINANCE_CHECKS', 'READY_TO_SUBMIT'),
  ('PROJECT_SETUP_FINANCE_CHECKS', 'ACCEPTED');

INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT null, 'project-created', NOW(), NOW(), 'FinanceCheckProcess', partnerorg.organisation_id, MIN(pu.id), MIN(a.id)
  FROM partner_organisation partnerorg
  JOIN project_user pu ON pu.project_id = partnerorg.project_id
                       AND pu.organisation_id = partnerorg.organisation_id
  JOIN activity_state a ON activity_type = 'PROJECT_SETUP_FINANCE_CHECKS' AND a.state = 'PENDING'
  GROUP BY partnerorg.organisation_id;