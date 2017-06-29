SELECT @app_via_id := id FROM activity_state where activity_type = 'PROJECT_SETUP_VIABILITY' AND state = 'ACCEPTED';

update process p, project_finance pf, partner_organisation po
set p.activity_state_id = @app_via_id,
p.event ='viability-approved',
p.last_modified = pf.viability_approval_date,
p.internal_participant_id = pf.viability_approval_user_id
where p.process_type = 'ViabilityProcess'
and pf.viability = 'APPROVED'
and po.project_id = pf.project_id
and po.organisation_id = pf.organisation_id
and p.target_id = po.id;