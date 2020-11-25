-- IFS-8787 subsidy controls

ALTER TABLE competition ADD COLUMN subsidy_control ENUM('STATE_AID', 'WTO_RULES', 'NOT_AID') NULL AFTER state_aid;

-- Migrate the full_application_finance field, but not for templates
UPDATE competition SET subsidy_control = 'STATE_AID' WHERE state_aid = 1;
UPDATE competition SET subsidy_control = 'NOT_AID' WHERE state_aid = 0;
