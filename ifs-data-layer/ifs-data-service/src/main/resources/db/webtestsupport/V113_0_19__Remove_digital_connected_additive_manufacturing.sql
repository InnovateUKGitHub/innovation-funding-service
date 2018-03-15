-- Remove the Competition 1 - based on the patch V107_2_0__Remove_old_competition.sql but this only purges the single Competition, not the templates

delete from form_input_validator where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1));

delete from guidance_row where id in (select gr.id from (select g.id from guidance_row g join form_input fi on fi.id = g.form_input_id join question q on q.id = fi.question_id where q.competition_id in (1)) as gr);

delete from form_input_response where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1));

delete from form_input where id in (select fit.id from (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1)) as fit);

delete from question where competition_id in (1);
delete from public_content where competition_id in (1);

SET foreign_key_checks = 0;
delete from section where competition_id in (1);
SET foreign_key_checks = 1;

delete from milestone where competition_id in (1);

delete from competition where id in (1);