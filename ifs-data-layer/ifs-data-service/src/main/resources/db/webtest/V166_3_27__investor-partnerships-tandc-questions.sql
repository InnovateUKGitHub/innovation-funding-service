UPDATE question
SET name = 'Investor Partnerships terms and conditions', short_name = 'Investor Partnerships terms and conditions', description = 'Investor Partnerships terms and conditions'
WHERE question_setup_type = 'TERMS_AND_CONDITIONS'
 AND competition_id = (select id from competition where funding_type= 'INVESTOR_PARTNERSHIPS');
