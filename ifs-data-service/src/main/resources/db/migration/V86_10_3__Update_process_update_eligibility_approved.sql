SELECT @app_eli_id := id FROM activity_state where activity_type = 'PROJECT_SETUP_ELIGIBILITY' AND state = 'ACCEPTED';

update process p, project_finance pf, partner_organisation po
set p.activity_state_id = @app_eli_id,
p.event ='eligibility-approved',
p.last_modified = pf.eligibility_approval_date,
p.internal_participant_id = pf.eligibility_approval_user_id
where p.process_type = 'EligibilityProcess'
and pf.eligibility = 'APPROVED'
and po.project_id = pf.project_id
and po.organisation_id = pf.organisation_id
and p.target_id = po.id;