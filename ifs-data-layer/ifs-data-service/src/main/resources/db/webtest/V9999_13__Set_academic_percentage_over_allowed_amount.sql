UPDATE finance_row SET cost = IF(cost IS NOT NULL, 50000, NULL) WHERE target_id = 4;
UPDATE competition SET max_research_ratio = 50 WHERE id = 8;