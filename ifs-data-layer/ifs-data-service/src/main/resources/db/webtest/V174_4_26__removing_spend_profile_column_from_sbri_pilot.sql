DELETE ps
FROM project_stages ps
INNER JOIN competition c on c.id = ps.competition_id
WHERE c.name='The Sustainable Innovation Fund: SBRI phase 1'
AND ps.project_setup_stage = 'SPEND_PROFILE';