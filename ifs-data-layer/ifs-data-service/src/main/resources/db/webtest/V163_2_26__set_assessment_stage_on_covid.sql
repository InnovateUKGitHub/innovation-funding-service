UPDATE competition SET has_assessment_stage=false WHERE name = 'Covid-19 competition';

SET @covid = (SELECT id FROM competition WHERE name = 'Covid-19 competition');
DELETE FROM competition_finance_row_types WHERE competition_id = @covid and finance_row_type='FINANCE';
INSERT INTO competition_finance_row_types(competition_id, finance_row_type) VALUES (@covid, 'GRANT_CLAIM_AMOUNT');