-- Remove the Competition 1 and all the competition templates. They now exist in the test data dump file

delete from form_input_validator where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3));

delete from guidance_row where id in (select gr.id from (select g.id from guidance_row g join form_input fi on fi.id = g.form_input_id join question q on q.id = fi.question_id where q.competition_id in (1,2,3)) as gr);

delete from form_input where id in (select fit.id from (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3)) as fit);

delete from question where competition_id in (1,2,3);
delete from public_content where competition_id in (1);

SET foreign_key_checks = 0;
delete from section where competition_id in (1,2,3);
SET foreign_key_checks = 1;

delete from milestone where competition_id in (1,2,3);

update competition_type set template_competition_id = NULL where name = 'Programme';
update competition_type set template_competition_id = NULL where name = 'Sector';

delete from competition where id = 1;
delete from competition where id = 2;
delete from competition where id = 3;