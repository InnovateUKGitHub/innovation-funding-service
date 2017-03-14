UPDATE process p INNER JOIN finance_check f ON p.target_id = f.id SET activity_state_id = 9 WHERE p.activity_state_id = 11 AND f.project_id = 6;

