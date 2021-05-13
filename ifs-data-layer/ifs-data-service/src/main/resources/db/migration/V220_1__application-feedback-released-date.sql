ALTER TABLE application
        ADD COLUMN feedback_released date DEFAULT NULL;

UPDATE application app
INNER JOIN milestone m ON app.competition = m.competition_id AND m.type = 'FEEDBACK_RELEASED'
SET app.feedback_released = m.date;