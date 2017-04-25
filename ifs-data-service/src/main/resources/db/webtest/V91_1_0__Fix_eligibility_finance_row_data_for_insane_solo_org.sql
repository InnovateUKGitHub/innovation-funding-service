SELECT @project_id := id from project WHERE name = 'Elbow grease';
SELECT @org_id_2 := id FROM organisation WHERE name = 'Big Riffs And Insane Solos Ltd';
SELECT @finance_id_2 := id FROM project_finance WHERE organisation_id = @org_id_2 AND project_id = @project_id;
UPDATE finance_row SET cost = 0 WHERE target_id = @finance_id_2 AND name = 'grant-claim';
UPDATE finance_row SET cost = 0 WHERE target_id = @finance_id_2 AND name = 'other-funding' AND description = 'Other Funding';
UPDATE finance_row SET cost = 1234 WHERE target_id = @finance_id_2 AND name = 'other-funding' AND description = 'Lottery';