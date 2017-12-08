-- Set Assessment form inputs to ensure correct rendering order

UPDATE form_input
SET priority = 1
WHERE scope = 'ASSESSMENT'
AND (description = 'Is the application in scope?' OR description = 'Feedback');

UPDATE form_input
SET priority = 2
WHERE scope = 'ASSESSMENT'
AND guidance_title = 'Guidance for assessing scope';