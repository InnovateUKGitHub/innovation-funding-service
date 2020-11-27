UPDATE competition 
SET gol_template_id = 50
WHERE funding_type != 'KTP';

UPDATE competition
SET gol_template_id = 51
WHERE funding_type = 'KTP';