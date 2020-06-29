-- competitions 2-9 are template competitions
-- our edi questions will go in the project details section, which are ids:
-- 19, 34, 49, 64, 66, 81, 96, 103

insert into question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
values (0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 2, 19, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 3, 34, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 4, 49, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 5, 64, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 6, 66, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 7, 81, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 8, 96, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, 'Equality, diversity and inclusion', 1, 0, 'Equality, diversity and inclusion', 'Equality, diversity and inclusion', 3, 9, 103, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION');

insert into form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, priority, question_id, scope, active)
values (2, 30, 2, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 2 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 3, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 3 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 4, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 4 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 5, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 5 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 6, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 6 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 7, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 7 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 8, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 8 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 9, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 9 and name = 'Equality, diversity and inclusion'), 'APPLICATION', 1);

insert into multiple_choice_option (text, form_input_id)
values ('Yes', (select id from form_input where competition_id = 2 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 2 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 3 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 3 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 4 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 4 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 5 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 5 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 6 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 6 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 7 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 7 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 8 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 8 and description = 'Equality, diversity and inclusion')),
('Yes', (select id from form_input where competition_id = 9 and description = 'Equality, diversity and inclusion')),
('No', (select id from form_input where competition_id = 9 and description = 'Equality, diversity and inclusion'));

