SELECT @na_via_id := id FROM activity_state where activity_type = 'PROJECT_SETUP_VIABILITY' AND state = 'NOT_APPLICABLE';

update process
set activity_state_id = @na_via_id,
event ='organisation-is-academic'
where process_type = 'ViabilityProcess'
and target_id in
	(select po.id
	from partner_organisation po, project_finance pf
	where pf.viability = 'NOT_APPLICABLE'
	and po.project_id = pf.project_id
	and po.organisation_id = pf.organisation_id);