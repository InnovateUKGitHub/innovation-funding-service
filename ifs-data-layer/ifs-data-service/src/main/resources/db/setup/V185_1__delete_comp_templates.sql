-- ifs-8314 deleting templates from db, as we now don't use the db for templates

update competition_type set template_competition_id = NULL;

delete from form_input_validator where form_input_id in (
  select fi.id from form_input fi
  join question q on q.id = fi.question_id
  where q.competition_id in (select id from competition where template = 1)
);

delete from appendix_file_types where form_input_id in (
  select fi.id from form_input fi
  join question q on q.id = fi.question_id
  where q.competition_id in (select id from competition where template = 1)
);

delete from multiple_choice_option where form_input_id in (
  select fi.id from form_input fi
  join question q on q.id = fi.question_id
  where q.competition_id in (select id from competition where template = 1)
);

delete from guidance_row where form_input_id in (
  select fi.id from form_input fi
  join question q on q.id = fi.question_id
  where q.competition_id in (select id from competition where template = 1)
);

delete from form_input where question_id in (
  select q.id from question q join section s on s.id = q.section_id where s.competition_id in (select id from competition where template = 1)
);

delete from question where section_id in (
  select id from section
  where competition_id in (select id from competition where template = 1)
);

SET foreign_key_checks = 0;
delete from section where competition_id in (
  select id from competition where template = 1
);
SET foreign_key_checks = 1;

delete from document_config_file_type where document_config_id in (
  select id from document_config where competition_id in (select id from competition where template = 1)
);

delete from document_config where competition_id in (select id from competition where template = 1);

delete from competition_finance_row_types where competition_id in (select id from competition where template = 1);

delete from project_stages where competition_id in (select id from competition where template = 1);

delete from grant_claim_maximum_competition where competition_id in (select id from competition where template = 1);

SET foreign_key_checks = 0;
delete from competition_application_config where id in (select competition_application_config_id from competition where template = 1);
delete from competition_assessment_config where id in (select competition_assessment_config_id from competition where template = 1);
delete from competition_organisation_config where id in (select competition_organisation_config_id from competition where template = 1);
delete from competition where template = 1;
SET foreign_key_checks = 1;


