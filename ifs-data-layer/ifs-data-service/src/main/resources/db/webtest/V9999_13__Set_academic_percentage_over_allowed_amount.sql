SELECT @project_id := id from project WHERE name = 'Elbow grease';
UPDATE finance_row SET cost = IF(cost IS NOT NULL, 50000, NULL) WHERE target_id = @project_id;
UPDATE competition SET max_research_ratio = 50 WHERE id = 8;