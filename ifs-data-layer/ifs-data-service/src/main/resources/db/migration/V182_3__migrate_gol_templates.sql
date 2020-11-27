UPDATE competition 
SET gol_template_id = 38
WHERE funding_type != 'KTP';

UPDATE competition
SET gol_template_id = 39
WHERE funding_type = 'KTP';