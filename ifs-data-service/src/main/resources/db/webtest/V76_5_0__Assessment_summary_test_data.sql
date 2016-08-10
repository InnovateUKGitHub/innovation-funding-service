-- Add additional questions for Competition 2
INSERT INTO question (id, assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_number, competition_id, section_id, assessor_maximum_score, question_type) VALUES (168, false, null, true, false, 'What mediums can you juggle with?', 'Mediums', 6, '2', 2, 71, 10, 'GENERAL');
INSERT INTO question (id, assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_number, competition_id, section_id, assessor_maximum_score, question_type) VALUES (169, false, null, true, false, 'What is your preferred juggling pattern?', 'Preferences', 7, '3', 2, 71, 10, 'GENERAL');
INSERT INTO question (id, assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, question_number, competition_id, section_id, assessor_maximum_score, question_type) VALUES (170, false, null, true, false, 'What do you wear when juggling?', 'Attire', 8, '4', 2, 71, 10, 'GENERAL');

-- Add form inputs
-- Q168
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (217, 100, 2, 2, 1, 'What mediums can you juggle with?', '', '<p>guidance</p>', 0, 168, 'APPLICATION');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (218, NULL, 23 , 2, 0, 'Question score', NULL, NULL, 0 , 168, 'ASSESSMENT');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (219, 100, 2, 2, 0, 'Feedback', 'Guidance for assessing mediums', '<p>Higher scores should be given for mediums with an element of danger such as fire and knives.</p>', 1, 168, 'ASSESSMENT');
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (218,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (219,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (219,3);
-- Q169
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (220, 100, 2, 2, 1, 'What is your preferred juggling pattern?', '', '<p>guidance</p>', 0, 169, 'APPLICATION');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (221, NULL, 23 , 2, 0, 'Question score', NULL, NULL, 0, 169, 'ASSESSMENT');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (222, 100, 2, 2, 0, 'Feedback', 'Guidance for assessing preferences', '<p>Award a higher score for more obscure patterns. The highest score should be reserved for multiplex patterns.</p>', 1, 169, 'ASSESSMENT');
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (221,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (222,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (222,3);
-- Q170
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (223, 100, 2, 2, 1, 'What do you wear when juggling?', '', '<p>guidance</p>', 0, 170, 'APPLICATION');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (224, NULL, 23 , 2, 0, 'Question score', NULL, NULL, 0, 170, 'ASSESSMENT');
INSERT INTO form_input (id, word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_question, guidance_answer, priority, question_id, scope) VALUES (225, 100, 2, 2, 0, 'Feedback', 'Guidance for assessing attire', '<p>Award additional points for fun alternatives to clown and court jester outfits, and for demonstrating originality.</p>', 1, 170, 'ASSESSMENT');
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (224,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (225,2);
INSERT INTO form_input_validator (form_input_id, form_validator_id) VALUES (225,3);

-- Update Applicant responses
UPDATE form_input_response SET value='This is the applicant response from Test One for Project Summary.' WHERE form_input_id=44 and application_id=8;
UPDATE form_input_response SET value='This is the applicant response from Test Two for Project Summary.' WHERE form_input_id=44 and application_id=9;
UPDATE form_input_response SET value='This is the applicant response from Test Three for Project Summary.' WHERE form_input_id=44 and application_id=10;
UPDATE form_input_response SET value='This is the applicant response from Test Five for Project Summary.' WHERE form_input_id=44 and application_id=12;
UPDATE form_input_response SET value='This is the applicant response from Test Seven for Project Summary.' WHERE form_input_id=44 and application_id=14;

UPDATE form_input_response SET value='This is the applicant response from Test One for Public Description.' WHERE form_input_id=45 and application_id=8;
UPDATE form_input_response SET value='This is the applicant response from Test Two for Public Description.' WHERE form_input_id=45 and application_id=9;
UPDATE form_input_response SET value='This is the applicant response from Test Three for Public Description.' WHERE form_input_id=45 and application_id=10;
UPDATE form_input_response SET value='This is the applicant response from Test Five for Public Description.' WHERE form_input_id=45 and application_id=12;
UPDATE form_input_response SET value='This is the applicant response from Test Seven for Public Description.' WHERE form_input_id=45 and application_id=14;

UPDATE form_input_response SET value='This is the applicant response from Test One for Scope.' WHERE form_input_id=46 and application_id=8;
UPDATE form_input_response SET value='This is the applicant response from Test Two for Scope.' WHERE form_input_id=46 and application_id=9;
UPDATE form_input_response SET value='This is the applicant response from Test Three for Scope.' WHERE form_input_id=46 and application_id=10;
UPDATE form_input_response SET value='This is the applicant response from Test Seven for Scope.' WHERE form_input_id=46 and application_id=14;

UPDATE form_input_response SET value='This is the applicant response from Test One for How Many.' WHERE form_input_id=47 and application_id=8;
UPDATE form_input_response SET value='This is the applicant response from Test Two for How Many.' WHERE form_input_id=47 and application_id=9;
UPDATE form_input_response SET value='This is the applicant response from Test Three for How Many.' WHERE form_input_id=47 and application_id=10;
UPDATE form_input_response SET value='This is the applicant response from Test Seven for How Many.' WHERE form_input_id=47 and application_id=14;

-- Add New Applicant responses
-- Submitted applications
-- Application 8 (Submitted)
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (114, '2016-04-25 13:04:50', 'This is the applicant response from Test One for Mediums.', 217, 29, 8, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (115, '2016-04-25 13:06:00', 'This is the applicant response from Test One for Preferences.', 220, 29, 8, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (116, '2016-04-25 13:07:55', 'This is the applicant response from Test One for Attire.', 223, 29, 8, null);

-- Application 9 (Approved)
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (117, '2016-04-25 13:08:03', 'This is the applicant response from Test Two for Mediums.', 217, 30, 9, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (118, '2016-04-25 13:09:51', 'This is the applicant response from Test Two for Preferences.', 220, 30, 9, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (119, '2016-04-25 13:11:14', 'This is the applicant response from Test Two for Attire.', 223, 30, 9, null);

-- Application 10 (Rejected)
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (120, '2016-04-25 13:09:13', 'This is the applicant response from Test Three for Mediums.', 217, 31, 10, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (121, '2016-04-25 13:10:59', 'This is the applicant response from Test Three for Preferences.', 220, 31, 10, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (122, '2016-04-25 13:12:07', 'This is the applicant response from Test Three for Attire.', 223, 31, 10, null);

-- Application 14 (Submitted)
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (123, '2016-04-25 13:25:53', 'This is the applicant response from Test Seven for Mediums.', 217, 35, 14, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (124, '2016-04-25 13:27:04', 'This is the applicant response from Test Seven for Preferences.', 220, 35, 14, null);
INSERT INTO form_input_response (id, update_date, value, form_input_id, updated_by_id, application_id, file_entry_id) VALUES (125, '2016-04-25 13:29:57', 'This is the applicant response from Test Seven for Attire.', 223, 35, 14, null);

-- Add Assessor responses for Assessment 10 (Open)
-- Q168
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (129, '6', 10, 218, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (130, 'This is the feedback from Professor Plum for Mediums.', 10, 219, '2016-07-15 11:41:36');

-- Q169
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (131, '4', 10, 221, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (132, 'This is the feedback from Professor Plum for Preferences.', 10, 222, '2016-07-15 11:41:36');

-- Q170
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (133, '9', 10, 224, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (134, 'This is the feedback from Professor Plum for Attire.', 10, 225, '2016-07-15 11:41:36');

-- Add Assessor responses for Assessment 11 (Open)
-- Q168
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (135, '10', 11, 218, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (136, 'This is the feedback from Felix Wilson for Mediums.', 11, 219, '2016-07-15 11:41:36');

-- Q169
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (137, '5', 11, 221, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (138, 'This is the feedback from Felix Wilson for Preferences.', 11, 222, '2016-07-15 11:41:36');

-- Q170
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (139, '7', 11, 224, '2016-07-15 11:41:36');
INSERT INTO assessor_form_input_response (id, value, assessment_id, form_input_id, updated_date) VALUES (140, 'This is the feedback from Felix Wilson for Attire.', 11, 225, '2016-07-15 11:41:36');