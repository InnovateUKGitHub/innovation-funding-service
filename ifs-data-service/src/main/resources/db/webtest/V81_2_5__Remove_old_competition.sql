delete from form_input_validator where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id = 1);

delete from form_input where id in (select fit.id from (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id = 1) as fit);

delete from question where competition_id = 1;

SET foreign_key_checks = 0;
delete from section where competition_id = 1;
SET foreign_key_checks = 1;

delete from milestone where competition_id = 1;

delete from competition where id = 1;