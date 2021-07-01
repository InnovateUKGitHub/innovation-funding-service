-- IFS-8453 - update KTP competition to have All finance view for assessor

UPDATE competition_assessment_config cac, competition comp
SET cac.assessor_finance_view = 'ALL', comp.assessor_finance_view = 'ALL'
WHERE comp.funding_type = 'KTP' AND cac.id = comp.competition_assessment_config_id;
