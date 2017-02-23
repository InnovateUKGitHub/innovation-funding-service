/** Set Ludlow on project 4 in test data to be a partner not requesting any funding for testing **/
UPDATE finance_row SET quantity = 0 WHERE description='Grant Claim'
AND target_id = (SELECT id FROM application_finance
                    WHERE application_id = (SELECT id FROM application WHERE name = 'Magic Material')
                    AND organisation_id = (SELECT id FROM organisation WHERE name = 'Ludlow'));