INSERT INTO project_stages (competition_id, project_setup_stage)

SELECT c.id                 as competition_id,
       'DOCUMENTS'          as project_setup_stage
FROM competition c
WHERE NOT EXISTS (select inner_ps.id from project_stages inner_ps where inner_ps.project_setup_stage = 'DOCUMENTS' and inner_ps.competition_id = c.id)
AND c.funding_type = 'PROCUREMENT';