-- add an FEEDBACK_RELEASED milestone for each competition that has a FEEDBACK_RELEASE in the past
INSERT INTO milestone (date, type, competition_id)
  SELECT m.date, 'FEEDBACK_RELEASED', competition_id FROM competition c
  INNER JOIN milestone m ON (m.competition_id = c.id)
  WHERE m.type='RELEASE_FEEDBACK' AND
    m.date < CURRENT_TIMESTAMP AND
    NOT EXISTS (
      SELECT * FROM competition c1
        INNER JOIN milestone m1 ON (m1.competition_id = c1.id)
      WHERE c.id = c1.id AND m1.type = 'FEEDBACK_RELEASED'
  );


