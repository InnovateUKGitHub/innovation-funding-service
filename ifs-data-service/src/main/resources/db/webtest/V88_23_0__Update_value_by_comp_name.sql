SELECT @project_id := id from project WHERE name = 'Elbow grease';
SELECT @org_id := id FROM organisation WHERE name = 'Aberystwyth University';
SELECT @finance_id := id FROM project_finance WHERE organisation_id = @org_id AND project_id = @project_id;
UPDATE finance_row SET cost = IF(cost IS NOT NULL, 30000, NULL) WHERE target_id = @finance_id;

UPDATE finance_row SET cost = IF(cost IS NOT NULL, 1000, NULL) WHERE target_id = 4;

UPDATE competition SET max_research_ratio = 0 WHERE id = 8;
UPDATE competition SET max_research_ratio = 50 WHERE name = 'New designs for a circular economy';


