-- if all spend profiles for a project APPROVED or REJECTED set process state accordingly,
-- if if all spend profiles marked as complete and project spend profile submitted date is set then set state to SUBMITTED
-- if spend profile present for all partners set state to CREATED
-- all others to PENDING
INSERT INTO process (end_date, event, last_modified, start_date, process_type, target_id, participant_id, activity_state_id)
  SELECT null, d.event, NOW(), NOW(), 'SpendProfileProcess', d.target_id, d.participant_id, a.id
  FROM (
    SELECT po.project_id AS target_id, pu.id AS participant_id,
        CASE
            WHEN sp.sp_count = po.partner_count AND sp.sp_count = spa.sp_approved_count THEN 'spend-profile-approved'
            WHEN sp.sp_count = po.partner_count AND sp.sp_count = spr.sp_rejected_count THEN 'spend-profile-rejected'
            WHEN sp.sp_count = po.partner_count AND sp.sp_count = sprtc.sp_ready_to_submit_count AND p.spend_profile_submitted_date IS NOT NULL THEN 'spend-profile-submitted'
            WHEN sp.sp_count = po.partner_count THEN 'spend-profile-generated'
            ELSE 'project-created'
        END AS event
    FROM (SELECT project_id, COUNT(organisation_id) AS partner_count FROM partner_organisation GROUP BY project_id) AS po
    LEFT JOIN (SELECT project_id, COUNT(organisation_id) AS sp_count FROM spend_profile GROUP BY project_id) AS sp ON sp.project_id = po.project_id
    LEFT JOIN (SELECT project_id, COUNT(organisation_id) AS sp_approved_count FROM spend_profile WHERE approval='APPROVED' GROUP BY project_id) AS spa ON sp.project_id = spa.project_id
    LEFT JOIN (SELECT project_id, COUNT(organisation_id) AS sp_rejected_count FROM spend_profile WHERE approval='REJECTED' GROUP BY project_id) AS spr ON sp.project_id = spr.project_id
    LEFT JOIN (SELECT project_id, COUNT(organisation_id) AS sp_ready_to_submit_count FROM spend_profile WHERE approval='UNSET' AND marked_as_complete ='1' GROUP BY project_id) AS sprtc ON sp.project_id = sprtc.project_id
    JOIN project p ON p.id = po.project_id
    JOIN application app ON app.id = p.application_id
    JOIN process_role pr ON pr.application_id = app.id AND pr.role_id = 1
    JOIN project_user pu ON pu.project_id = p.id AND pu.user_id = pr.user_id and pu.project_role = 'PROJECT_PARTNER'
  ) AS d
  JOIN activity_state a ON activity_type = 'PROJECT_SETUP_SPEND_PROFILE' AND
    a.state = CASE
        WHEN d.event = 'project-created' THEN 'PENDING'
        WHEN d.event = 'spend-profile-approved' THEN 'ACCEPTED'
        WHEN d.event = 'spend-profile-rejected' THEN 'REJECTED'
        WHEN d.event = 'spend-profile-submitted' THEN 'SUBMITTED'
        WHEN d.event = 'spend-profile-generated' THEN 'CREATED'
    END
  WHERE NOT EXISTS (SELECT 1 FROM process WHERE process.target_id = d.target_id AND process.process_type = 'SpendProfileProcess');
