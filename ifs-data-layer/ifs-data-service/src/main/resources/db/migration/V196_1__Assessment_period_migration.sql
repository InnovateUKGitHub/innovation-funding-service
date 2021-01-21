INSERT INTO assessment_period
SELECT
1,
c.id
FROM competition c
JOIN milestone m ON c.id = m.competition_id
WHERE c.always_open IS NULL OR c.always_open = 0 AND m.type = 'ASSESSOR_BRIEFING';

UPDATE Milestone m
JOIN competition c ON m.competition_id = c.id
JOIN assessment_period a ON m.competition_id = a.competition_id
SET m.parent_id = a.id
WHERE m.type IN ('ASSESSOR_BRIEFING', 'ASSESSOR_ACCEPTS', 'ASSESSOR_DEADLINE');

