-- IFS-6292 - Move viability for any existing LOAN projects to COMPLETE_OFFLINE

UPDATE process proc
    INNER JOIN partner_organisation po ON po.id=proc.target_id
    INNER JOIN project p on po.project_id = p.id
    INNER JOIN application a ON a.id = p.application_id
    INNER JOIN competition c ON c.id = a.competition
SET activity_state_id = (SELECT id FROM activity_state WHERE activity_type = 'PROJECT_SETUP_VIABILITY' AND state = 'COMPLETED_OFFLINE')
    WHERE proc.process_type = 'ViabilityProcess' AND c.funding_type = 'LOAN';