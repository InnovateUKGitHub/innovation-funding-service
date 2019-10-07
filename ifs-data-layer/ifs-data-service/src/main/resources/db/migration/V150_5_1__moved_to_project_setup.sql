
ALTER TABLE competition ADD COLUMN project_setup_started datetime;

UPDATE competition c
inner join application app on app.competition = c.id
inner join project p on p.application_id=app.id
SET c.project_setup_started = (select min(manage_funding_email_date) from application where competition = c.id)
where p.id is not null;