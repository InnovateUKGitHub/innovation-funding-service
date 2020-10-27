UPDATE section s
    JOIN competition c on s.competition_id = c.id
SET s.assessor_guidance_description = ''
WHERE c.funding_type = 'KTP'
  AND s.name = 'Application questions';