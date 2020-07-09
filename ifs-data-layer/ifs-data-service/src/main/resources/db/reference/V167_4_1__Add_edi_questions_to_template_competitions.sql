
update question set priority = 3 where competition_id in (select id from competition where template = 1) and question_setup_type = 'APPLICATION_DETAILS';

insert into question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_type, question_setup_type, competition_id, section_id)
select 0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION', c.id as competition_id, s.id as section_id
FROM section s, competition c
where s.competition_id = c.id and s.name = 'Project details' and c.template = 1;

insert into form_input (word_count, form_input_type_id, included_in_application_summary, description, priority, scope, active, competition_id, question_id)
select 2, 30, 1, 'Equality, diversity and inclusion', 0, 'APPLICATION', 1, c.id as competition_id, q.id as question_id
FROM competition c, question q
where q.competition_id = c.id and q.short_name = 'Equality, diversity and inclusion' and c.template = 1;

insert into form_input_validator (form_validator_id, form_input_id)
select 11, fi.id as form_input_id
from form_input fi, competition c
where fi.competition_id = c.id and c.template = 1 and fi.description = 'Equality, diversity and inclusion';

insert into multiple_choice_option (text, form_input_id)
select 'Yes', fi.id as form_input_id
from form_input fi, competition c
where fi.competition_id = c.id and c.template = 1 and fi.description = 'Equality, diversity and inclusion';

insert into multiple_choice_option (text, form_input_id)
select 'No', fi.id as form_input_id
from form_input fi, competition c
where fi.competition_id = c.id and c.template = 1 and fi.description = 'Equality, diversity and inclusion';

