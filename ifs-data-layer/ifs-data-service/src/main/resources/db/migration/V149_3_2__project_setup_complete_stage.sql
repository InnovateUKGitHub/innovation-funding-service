-- IFS-6285 new project setup stage

ALTER TABLE project_stages MODIFY project_setup_stage enum (
    'PROJECT_DETAILS',
    'PROJECT_TEAM',
    'DOCUMENTS',
    'MONITORING_OFFICER',
    'BANK_DETAILS',
    'FINANCE_CHECKS',
    'SPEND_PROFILE',
    'GRANT_OFFER_LETTER',
    'PROJECT_SETUP_COMPLETE'
  ) NOT NULL;

-- Update existing loan comps.
INSERT INTO project_stages (competition_id, project_setup_stage)
SELECT id as competition_id, 'PROJECT_SETUP_COMPLETE' as project_setup_stage
FROM competition
WHERE funding_type = "LOAN";