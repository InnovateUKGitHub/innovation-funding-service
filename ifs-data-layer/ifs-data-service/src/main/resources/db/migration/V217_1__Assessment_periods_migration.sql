ALTER TABLE assessment_period DROP COLUMN rank;

INSERT INTO assessment_period (competition_id)
SELECT
c.id as competition_id
FROM competition c
INNER JOIN milestone m ON c.id = m.competition_id
WHERE c.always_open IS NULL OR c.always_open = 0 AND m.type = 'ASSESSOR_BRIEFING';

UPDATE milestone m
INNER JOIN competition c ON m.competition_id = c.id
INNER JOIN assessment_period ap ON m.competition_id = ap.competition_id
SET m.parent_id = ap.id
WHERE m.type IN ('ASSESSOR_BRIEFING', 'ASSESSOR_ACCEPTS', 'ASSESSOR_DEADLINE', 'ASSESSORS_NOTIFIED', 'ASSESSMENT_CLOSED');

UPDATE application a
INNER JOIN competition c ON a.competition = c.id
INNER JOIN assessment_period ap ON c.id = ap.competition_id
SET a.assessment_period_id = ap.id;

