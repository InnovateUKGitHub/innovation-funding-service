ALTER TABLE grant_claim_maximum
ADD COLUMN funding_rules ENUM('STATE_AID', 'SUBSIDY_CONTROL', 'NOT_AID') NULL;

ALTER TABLE application_finance
ADD COLUMN northern_ireland_declaration BOOLEAN NULL;

ALTER TABLE project_finance
ADD COLUMN northern_ireland_declaration BOOLEAN NULL;