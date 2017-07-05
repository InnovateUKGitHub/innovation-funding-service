-- add an ASSESSORS_NOTIFIED milestone for each competition that doesn't already have one, based on an existing ASSESSOR_ACCEPTS milestone
INSERT INTO milestone (date, type, competition_id)
  SELECT m.date, 'ASSESSORS_NOTIFIED', competition_id FROM competition c
  INNER JOIN milestone m ON (m.competition_id = c.id)
  WHERE m.type='ASSESSOR_ACCEPTS' AND NOT EXISTS (
      SELECT * FROM competition c1
        INNER JOIN milestone m1 ON (m1.competition_id = c1.id)
      WHERE c.id = c1.id AND m1.type = 'ASSESSORS_NOTIFIED'
  );

-- add an ASSESSMENT_CLOSED milestone for each competition that doesn't already have one, based on an existing ASSESSOR_DEADLINE milestone
INSERT INTO milestone (date, type, competition_id)
  SELECT m.date, 'ASSESSMENT_CLOSED', competition_id FROM competition c
    INNER JOIN milestone m ON (m.competition_id = c.id)
  WHERE m.type='ASSESSOR_DEADLINE' AND NOT EXISTS (
      SELECT * FROM competition c1
        INNER JOIN milestone m1 ON (m1.competition_id = c1.id)
      WHERE c.id = c1.id AND m1.type = 'ASSESSMENT_CLOSED'
  );

