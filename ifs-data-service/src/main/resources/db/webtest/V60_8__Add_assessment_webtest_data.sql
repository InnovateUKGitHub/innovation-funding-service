-- Rename Second Question, Third Question, Fourth Question
UPDATE `question` SET `name`='Project summary', `short_name`='Project summary' WHERE `id` IN (11, 44, 69, 94, 119, 144);
UPDATE `question` SET `name`='Public description', `short_name`='Public description' WHERE `id` IN (12, 45, 70, 95, 120, 145);
UPDATE `question` SET `name`='How does your project align with the scope of this competition?', `short_name`='Scope' WHERE `id` IN (13, 46, 71, 96, 121, 146);
UPDATE `form_input` SET `description`='Project summary' WHERE `question_id` IN (44, 69, 94, 119, 144);
UPDATE `form_input` SET `description`='Public description' WHERE `question_id` IN (45, 70, 95, 120, 145);
UPDATE `form_input` SET `description`='How does your project align with the scope of this competition?' WHERE `question_id` IN (46, 71, 96, 121, 146);

-- Move Fifth Question to a new 'Application questions' section in each of the Competitions 2 through to 6
INSERT IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (71,'Each question should be given a score out of 10. Written feedback should also be given.','These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.',0,'Application questions',2,2,NULL,0,'GENERAL');
INSERT IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (72,'Each question should be given a score out of 10. Written feedback should also be given.','These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.',0,'Application questions',2,3,NULL,0,'GENERAL');
INSERT IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (73,'Each question should be given a score out of 10. Written feedback should also be given.','These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.',0,'Application questions',2,4,NULL,0,'GENERAL');
INSERT IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (74,'Each question should be given a score out of 10. Written feedback should also be given.','These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.',0,'Application questions',2,5,NULL,0,'GENERAL');
INSERT IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (75,'Each question should be given a score out of 10. Written feedback should also be given.','These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.',0,'Application questions',2,6,NULL,0,'GENERAL');

UPDATE `question` SET `section_id`=71 WHERE `id`=47;
UPDATE `question` SET `section_id`=72 WHERE `id`=72;
UPDATE `question` SET `section_id`=73 WHERE `id`=97;
UPDATE `question` SET `section_id`=74 WHERE `id`=122;
UPDATE `question` SET `section_id`=75 WHERE `id`=147;

-- Bump up the priority of the 'Finances' sections after inserting the new 'Application questions' section.
UPDATE `section` SET `priority`=3 WHERE `id` IN (17,28,39,50,61);

-- Rename Fifth Question on the Juggling Craziness competition
UPDATE `question` SET `name`='How many balls can you juggle?', `short_name`='How many' WHERE `id`=47;
UPDATE `form_input` SET `description`='How many balls can you juggle?' WHERE `question_id`=47;

-- Create new Organisation with Address to be associated with Assessors on the Juggling Craziness competition
INSERT IGNORE INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (28,'8','Juggle Square','','Romford','RM2 5SR','');
INSERT IGNORE INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (29,'9','Juggle Meadows','Droylsden','Manchester','M43 6QZ','');

INSERT IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (36,'In Juggling We Trust Ltd','06477798',NULL,1);
INSERT IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (37,'Juggling Buffoonery Ltd','06477798',NULL,1);

INSERT IGNORE INTO `organisation_address` (`id`, `address_type_id`, `address_id`, `organisation_id`) VALUES (28,1,28,36);
INSERT IGNORE INTO `organisation_address` (`id`, `address_type_id`, `address_id`, `organisation_id`) VALUES (29,1,29,37);

-- Assign Assessors on Applications 8 and 14
INSERT IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (63,8,36,3,3);
INSERT IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (64,14,36,3,3);
INSERT IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (65,8,37,3,9);
INSERT IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (66,14,37,3,9);

-- Create Open and Pending Assessments for Applications 8 and 14
INSERT IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (9,NULL,'','2016-07-11 12:08:36',NULL,'pending','Assessment',63);
INSERT IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (10,NULL,'accept','2016-07-12 17:13:04',NULL,'open','Assessment',64);
INSERT IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (11,NULL,'accept','2016-07-14 10:43:11',NULL,'open','Assessment',65);
INSERT IGNORE INTO `process` (`id`, `end_date`, `event`, `last_modified`, `start_date`, `status`, `process_type`, `process_role`) VALUES (12,NULL,'','2016-07-11 12:10:44',NULL,'pending','Assessment',66);

-- Create Assessor Form Inputs for each Question in the 'Project details' and 'Application questions' sections where appropriate

-- Competition 2
-- Q46
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (191,NULL,21,2,0,'Please select the research category for this project',NULL,NULL,0,46,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (192,NULL,22,2,0,'Is the application in scope?',NULL,NULL,1,46,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (193,100,2,2,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href="https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief">competition brief</a>.',2,46,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (191,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (192,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (193,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (193,3);

-- Q47
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (194,NULL,23,2,0,'Question score',NULL,NULL,0,47,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (195,100,2,2,0,'Feedback','Guidance for assessing number of balls','<p>More balls is better. Additional points for other objects and flames.</p>',1,47,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (194,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (195,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (195,3);

-- Competition 3
-- Q71
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (196,NULL,21,3,0,'Please select the research category for this project',NULL,NULL,0,71,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (197,NULL,22,3,0,'Is the application in scope?',NULL,NULL,1,71,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (198,100,2,3,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href="https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief">competition brief</a>.',2,71,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (196,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (197,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (198,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (198,3);

-- Q72
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (199,NULL,23,3,0,'Question score',NULL,NULL,0,72,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (200,100,2,3,0,'Feedback','Guidance for the fifth question','<p>Guidance for the fifth question...</p>',1,72,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (199,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (200,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (200,3);

-- Competition 4
-- Q96
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (201,NULL,21,4,0,'Please select the research category for this project',NULL,NULL,0,96,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (202,NULL,22,4,0,'Is the application in scope?',NULL,NULL,1,96,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (203,100,2,4,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href="https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief">competition brief</a>.',2,96,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (201,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (202,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (203,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (203,3);

-- Q97
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (204,NULL,23,4,0,'Question score',NULL,NULL,0,97,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (205,100,2,4,0,'Feedback','Guidance for the fifth question','<p>Guidance for the fifth question...</p>',1,97,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (204,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (205,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (205,3);

-- Competition 5
-- Q121
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (206,NULL,21,5,0,'Please select the research category for this project',NULL,NULL,0,121,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (207,NULL,22,5,0,'Is the application in scope?',NULL,NULL,1,121,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (208,100,2,5,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href="https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief">competition brief</a>.',2,121,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (206,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (207,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (208,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (208,3);

-- Q122
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (209,NULL,23,5,0,'Question score',NULL,NULL,0,122,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (210,100,2,5,0,'Feedback','Guidance for the fifth question','<p>Guidance for the fifth question...</p>',1,122,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (209,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (210,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (210,3);

-- Competition 6
-- Q146
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (211,NULL,21,6,0,'Please select the research category for this project',NULL,NULL,0,146,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (212,NULL,22,6,0,'Is the application in scope?',NULL,NULL,1,146,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (213,100,2,6,0,'Feedback','Guidance for assessing scope','<p>Your answer should be based upon the following:<table><tr><th scope="row">YES<td><p>The application contains the following:<ul><li>Is the consortia business led?<li>Are there two or more partners to the collaboration?<li>Does it meet the scope of the competition as defined in the competition brief?</ul><tr><th scope="row">NO<td><p>One or more of the above requirements have not been satisfied.</table><p class=extra-margin>Your assessment of the project scope should be based upon the <a href="https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief">competition brief</a>.',2,146,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (211,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (212,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (213,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (213,3);

-- Q147
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (214,NULL,23,6,0,'Question score',NULL,NULL,0,147,'ASSESSMENT');
INSERT IGNORE INTO `form_input` (`id`, `word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`, `guidance_question`, `guidance_answer`, `priority`, `question_id`, `scope`) VALUES (215,100,2,6,0,'Feedback','Guidance for the fifth question','<p>Guidance for the fifth question...</p>',1,147,'ASSESSMENT');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (214,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (215,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (215,3);

-- Add Assessor responses for Competition 1, Assessment 1 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (1,'8',1,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (2,'This is the feedback from Professor Plum for Business opportunity.',1,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (3,'8',1,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (4,'This is the feedback from Professor Plum for Potential market.',1,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (5,'7',1,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (6,'This is the feedback from Professor Plum for Project exploitation.',1,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (7,'6',1,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (8,'This is the feedback from Professor Plum for Economic benefit.',1,175,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (9,'9',1,176,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (10,'This is the feedback from Professor Plum for Technical approach.',1,177,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (11,'7',1,178,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (12,'This is the feedback from Professor Plum for Innovation.',1,179,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (13,'3',1,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (14,'This is the feedback from Professor Plum for Risks.',1,181,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (15,'10',1,182,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (16,'This is the feedback from Professor Plum for Project team.',1,183,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (17,'Industrial research',1,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (18,'true',1,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (19,'This is the feedback from Professor Plum for Scope.',1,186,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (20,'8',1,187,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (21,'This is the feedback from Professor Plum for Funding.',1,188,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (22,'6',1,189,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (23,'This is the feedback from Professor Plum for Adding value.',1,190,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 2 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (24,'3',2,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (25,'This is the feedback from Professor Plum for Business opportunity.',2,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (26,'7',2,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (27,'This is the feedback from Professor Plum for Potential market.',2,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (28,'3',2,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (29,'This is the feedback from Professor Plum for Project exploitation.',2,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (30,'3',2,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (31,'This is the feedback from Professor Plum for Economic benefit.',2,175,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (32,'5',2,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (33,'This is the feedback from Professor Plum for Risks.',2,181,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (34,'Technical feasibility',2,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (35,'true',2,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (36,'This is the feedback from Professor Plum for Scope.',2,186,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 3 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (37,'1',3,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (38,'This is the feedback from Professor Plum for Business opportunity.',3,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (39,'2',3,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (40,'This is the feedback from Professor Plum for Potential market.',3,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (41,'3',3,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (42,'This is the feedback from Professor Plum for Project exploitation.',3,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (43,'2',3,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (44,'This is the feedback from Professor Plum for Economic benefit.',3,175,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (45,'3',3,176,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (46,'This is the feedback from Professor Plum for Technical approach.',3,177,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (47,'4',3,178,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (48,'This is the feedback from Professor Plum for Innovation.',3,179,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (49,'1',3,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (50,'This is the feedback from Professor Plum for Risks.',3,181,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (51,'2',3,182,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (52,'This is the feedback from Professor Plum for Project team.',3,183,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (53,'Experimental development',3,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (54,'true',3,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (55,'This is the feedback from Professor Plum for Scope.',3,186,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (56,'2',3,187,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (57,'This is the feedback from Professor Plum for Funding.',3,188,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (58,'1',3,189,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (59,'This is the feedback from Professor Plum for Adding value.',3,190,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 5 (Submitted)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (60,'6',5,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (61,'This is the feedback from Felix Wilson for Business opportunity.',5,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (62,'9',5,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (63,'This is the feedback from Felix Wilson for Potential market.',5,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (64,'4',5,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (65,'This is the feedback from Felix Wilson for Project exploitation.',5,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (66,'9',5,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (67,'This is the feedback from Felix Wilson for Economic benefit.',5,175,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (68,'9',5,176,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (69,'This is the feedback from Felix Wilson for Technical approach.',5,177,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (70,'7',5,178,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (71,'This is the feedback from Felix Wilson for Innovation.',5,179,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (72,'2',5,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (73,'This is the feedback from Felix Wilson for Risks.',5,181,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (74,'9',5,182,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (75,'This is the feedback from Felix Wilson for Project team.',5,183,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (76,'Technical feasibility',5,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (77,'true',5,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (78,'This is the feedback from Felix Wilson for Scope.',5,186,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (79,'9',5,187,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (80,'This is the feedback from Felix Wilson for Funding.',5,188,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (81,'8',5,189,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (82,'This is the feedback from Felix Wilson for Adding value.',5,190,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 6 (Open)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (83,'7',6,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (84,'This is the feedback from Felix Wilson for Business opportunity.',6,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (85,'5',6,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (86,'This is the feedback from Felix Wilson for Potential market.',6,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (87,'4',6,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (88,'This is the feedback from Felix Wilson for Project exploitation.',6,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (89,'5',6,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (90,'This is the feedback from Felix Wilson for Economic benefit.',6,175,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (91,'8',6,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (92,'This is the feedback from Felix Wilson for Risks.',6,181,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (93,'Industrial research',6,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (94,'true',6,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (95,'This is the feedback from Felix Wilson for Scope.',6,186,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 1, Assessment 7 (Assessed)
-- Q1
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (96,'4',7,168,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (97,'This is the feedback from Felix Wilson for Business opportunity.',7,169,'2016-07-15 11:41:36');
-- Q2
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (98,'3',7,170,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (99,'This is the feedback from Professor Plum for Potential market.',7,171,'2016-07-15 11:41:36');
-- Q3
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (100,'1',7,172,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (101,'This is the feedback from Felix Wilson for Project exploitation.',7,173,'2016-07-15 11:41:36');
-- Q4
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (102,'5',7,174,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (103,'This is the feedback from Felix Wilson for Economic benefit.',7,175,'2016-07-15 11:41:36');
-- Q5
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (104,'1',7,176,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (105,'This is the feedback from Felix Wilson for Technical approach.',7,177,'2016-07-15 11:41:36');
-- Q6
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (106,'1',7,178,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (107,'This is the feedback from Felix Wilson for Innovation.',7,179,'2016-07-15 11:41:36');
-- Q7
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (108,'2',7,180,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (109,'This is the feedback from Felix Wilson for Risks.',7,181,'2016-07-15 11:41:36');
-- Q8
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (110,'2',7,182,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (111,'This is the feedback from Felix Wilson for Project team.',7,183,'2016-07-15 11:41:36');
-- Q13
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (112,'Experimental development',7,184,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (113,'true',7,185,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (114,'This is the feedback from Felix Wilson for Scope.',7,186,'2016-07-15 11:41:36');
-- Q15
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (115,'1',7,187,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (116,'This is the feedback from Felix Wilson for Funding.',7,188,'2016-07-15 11:41:36');
-- Q16
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (117,'1',7,189,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (118,'This is the feedback from Felix Wilson for Adding value.',7,190,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 2, Assessment 10 (Open)
-- Q46
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (119,'Industrial research',10,191,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (120,'true',10,192,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (121,'This is the feedback from Professor Plum for Scope.',10,193,'2016-07-15 11:41:36');

-- Q47
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (122,'8',10,194,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (123,'This is the feedback from Professor Plum for How many.',10,195,'2016-07-15 11:41:36');

-- Add Assessor responses for Competition 2, Assessment 11 (Open)
-- Q46
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (124,'Experimental development',11,191,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (125,'true',11,192,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (126,'This is the feedback from Felix Wilson for Scope.',11,193,'2016-07-15 11:41:36');

-- Q47
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (127,'5',11,194,'2016-07-15 11:41:36');
INSERT IGNORE INTO `assessor_form_input_response` (`id`, `value`, `assessment_id`, `form_input_id`, `updated_date`) VALUES (128,'This is the feedback from Felix Wilson for How many.',11,195,'2016-07-15 11:41:36');

-- Set the maximum assessor score for Questions

-- Competition 2
UPDATE `question` SET `assessor_maximum_score`=20 WHERE `id`=47;

-- Competition 3
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id`=72;

-- Competition 4
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id`=97;

-- Competition 5
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id`=122;

-- Competition 6
UPDATE `question` SET `assessor_maximum_score`=10 WHERE `id`=147;