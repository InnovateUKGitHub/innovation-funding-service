UPDATE competition SET covid_type='DE_MINIMIS' WHERE name = '583 Covid deminis round 1';
UPDATE competition SET covid_type='ADDITIONAL_FUNDING' WHERE name = '596 Covid grants framework group';
UPDATE competition SET covid_type='DE_MINIMIS_ROUND_2' WHERE name = '599 Covid de minimis round 2';

UPDATE competition SET has_assessment_stage=false WHERE covid_type = 'ADDITIONAL_FUNDING';
SET @additional_funding_id = (SELECT id FROM competition WHERE covid_type = 'ADDITIONAL_FUNDING');
DELETE FROM competition_finance_row_types WHERE competition_id = @additional_funding_id and finance_row_type='FINANCE';
INSERT INTO competition_finance_row_types(competition_id, finance_row_type) VALUES (@additional_funding_id, 'GRANT_CLAIM_AMOUNT');