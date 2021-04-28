ALTER TABLE application
        ADD COLUMN assessment_period_id date DEFAULT NULL;

UPDATE application app
SET app.assessment_period_id = m.date
INNER JOIN milestone m ON app.competition = m.competition_id AND m.type = 'FEEDBACK_RELEASED';