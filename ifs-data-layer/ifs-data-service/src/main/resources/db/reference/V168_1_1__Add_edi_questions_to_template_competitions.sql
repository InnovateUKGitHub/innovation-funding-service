-- competitions 2-9 are template competitions
-- our edi questions will go in the project details section, which are ids:
-- 19, 34, 49, 64, 66, 81, 96, 103

update question set priority = 3 where competition_id in (2,3,4,5,6,7,8,9) and question_setup_type = 'APPLICATION_DETAILS';

insert into question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
values (0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 2, 19, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 3, 34, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 4, 49, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 5, 64, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 6, 66, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 7, 81, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 8, 96, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION'),
(0, '<a href="https://www.surveymonkey.co.uk/r/ifsaccount" target="_blank" rel="external">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>', 1, 0, 'Have you completed the EDI survey?', 'Equality, diversity and inclusion', 2, 9, 103, 'GENERAL', 'EQUALITY_DIVERSITY_INCLUSION');

insert into form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, priority, question_id, scope, active)
values (2, 30, 2, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 2 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 3, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 3 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 4, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 4 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 5, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 5 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 6, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 6 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 7, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 7 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 8, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 8 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1),
(2, 30, 9, 1, 'Equality, diversity and inclusion', 0, (select id from question where competition_id = 9 and short_name = 'Equality, diversity and inclusion'), 'APPLICATION', 1);

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

