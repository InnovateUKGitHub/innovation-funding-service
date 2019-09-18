-- Remove the Competition 1 and all the competition templates. They now exist in the test data dump file
SET foreign_key_checks = 0;

delete from form_input_validator where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3,4,5,6,7,8,9));

delete from guidance_row where id in (select gr.id from (select g.id from guidance_row g join form_input fi on fi.id = g.form_input_id join question q on q.id = fi.question_id where q.competition_id in (1,2,3,4,5,6,7,8,9)) as gr);

delete from form_input_response where form_input_id in (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3,4,5,6,7,8,9));

delete from appendix_file_types where form_input_id in (select fit.id from (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3,4,5,6,7,8,9)) as fit);

delete from form_input where id in (select fit.id from (select fi.id from form_input fi join question q on q.id = fi.question_id where q.competition_id in (1,2,3,4,5,6,7,8,9)) as fit);

delete from document_config_file_type where document_config_id in (select id from document_config where competition_id in (1,2,3,4,5,6,7,8,9));

delete from document_config where competition_id in (1,2,3,4,5,6,7,8,9);

delete from grant_claim_maximum_competition where competition_id in (1,2,3,4,5,6,7,8,9);

delete from file_type;

delete from schedule_status;

delete from question where competition_id in (1,2,3,4,5,6,7,8,9);
delete from public_content where competition_id in (1);

delete from setup_status where target_id in (1,2,3,4,5,6,7,8,9) and target_class_name = 'org.innovateuk.ifs.competition.domain.Competition';

delete from section where competition_id in (1,2,3,4,5,6,7,8,9);

delete from competition_finance_row_types where competition_id in (1,2,3,4,5,6,7,8,9);

delete from milestone where competition_id in (1,2,3,4,5,6,7,8,9);

delete from project_stages where competition_id in (1,2,3,4,5,6,7,8,9);

update competition_type set template_competition_id = NULL where name = 'Programme';
update competition_type set template_competition_id = NULL where name = 'Sector';
update competition_type set template_competition_id = NULL where name = 'Generic';
update competition_type set template_competition_id = NULL where name = 'Expression of interest';
update competition_type set template_competition_id = NULL where name = 'Advanced Propulsion Centre';
update competition_type set template_competition_id = NULL where name = 'Aerospace Technology Institute';
update competition_type set template_competition_id = NULL where name = "The Prince's Trust";
update competition_type set template_competition_id = NULL where name = 'Horizon 2020';

delete from competition where id in (1,2,3,4,5,6,7,8,9);

SET foreign_key_checks = 1;