-- IFS-6284 Adding date competition moved to PS tab.

ALTER TABLE competition ADD COLUMN project_setup_started datetime;

UPDATE competition c
INNER JOIN application app ON app.competition = c.id
INNER JOIN project p ON p.application_id=app.id
SET c.project_setup_started = (SELECT MIN(manage_funding_email_date) FROM application WHERE competition = c.id)
WHERE p.id IS NOT NULL;