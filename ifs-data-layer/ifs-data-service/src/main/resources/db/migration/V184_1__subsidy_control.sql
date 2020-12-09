-- IFS-8787 subsidy controls

ALTER TABLE competition ADD COLUMN funding_rules ENUM('STATE_AID', 'SUBSIDY_CONTROL', 'NOT_AID') NULL AFTER state_aid;

UPDATE competition SET funding_rules = 'STATE_AID' WHERE state_aid = 1;
UPDATE competition SET funding_rules = 'NOT_AID' WHERE state_aid = 0;
