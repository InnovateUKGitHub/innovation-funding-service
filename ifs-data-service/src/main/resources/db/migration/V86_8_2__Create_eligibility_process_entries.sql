INSERT INTO activity_state (activity_type, state) VALUES
  ('PROJECT_SETUP_ELIGIBILITY', 'NOT_VERIFIED'),
  ('PROJECT_SETUP_ELIGIBILITY', 'NOT_APPLICABLE'),
  ('PROJECT_SETUP_ELIGIBILITY', 'ACCEPTED');

INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
    SELECT null, 'project-created', NOW(), NOW(), 'EligibilityProcess', partnerorg.id, MIN(pu.id), MIN(a.id)
    FROM partner_organisation partnerorg
    JOIN project_user pu ON pu.project_id = partnerorg.project_id
                         AND pu.organisation_id = partnerorg.organisation_id
    JOIN activity_state a ON activity_type = 'PROJECT_SETUP_ELIGIBILITY' AND a.state = 'NOT_VERIFIED'
    GROUP BY partnerorg.id;