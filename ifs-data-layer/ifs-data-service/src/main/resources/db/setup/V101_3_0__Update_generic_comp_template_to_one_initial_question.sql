/* Find the current the generic competition type and project details section ids */
SET @generic_template_id = (SELECT `id` FROM competition WHERE name='Template for the Generic competition type');
SET @project_details_questions_section = (SELECT `id` FROM section WHERE name='Application questions' AND competition_id=@generic_template_id);

/* Remove all current application questions and attached validators/guidance rows from generic competition type */
#Remove guidance rows for generic competition project detail questions form inputs
DELETE FROM `guidance_row` WHERE form_input_id IN (
    SELECT form_input.id FROM form_input
    LEFT JOIN question ON (form_input.question_id = question.id)
    WHERE question.competition_id = @generic_template_id AND question.section_id=@project_details_questions_section);

#Remove validators for generic competition project detail questions form inputs
DELETE FROM `form_input_validator` WHERE form_input_id IN (
    SELECT form_input.id FROM form_input
    LEFT JOIN question ON (form_input.question_id = question.id)
    WHERE question.competition_id = @generic_template_id AND question.section_id=@project_details_questions_section);

#Remove form_inputs for generic competition project detail questions
DELETE FROM `form_input` WHERE question_id IN (
    SELECT id FROM question WHERE competition_id = @generic_template_id AND section_id=@project_details_questions_section);

#Remove project detail questions from generic competition
DELETE FROM `question` WHERE competition_id = @generic_template_id AND section_id=@project_details_questions_section;

/* Add new initial application question to generic competition type */
#Find validator ids
SET @not_empty_validator = (SELECT `id` FROM form_validator WHERE `clazz_name` = 'org.innovateuk.ifs.validator.NotEmptyValidator');
SET @word_count_validator = (SELECT `id` FROM form_validator WHERE `clazz_name` = 'org.innovateuk.ifs.validator.WordCountValidator');

#Insert question and corresponding table rows
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,1,0,NULL,NULL,5,1,@generic_template_id,@project_details_questions_section,10,'GENERAL');
SET @q_initial_question=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@generic_template_id,1,NULL,NULL,NULL,0,@q_initial_question,'APPLICATION',1);
SET @main_initial_question=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@generic_template_id,0,'Question score',NULL,NULL,0,@q_initial_question,'ASSESSMENT',1);
SET @score_initial_question=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@generic_template_id,1,'Appendix','What should I include in the appendix?','<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_initial_question,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@generic_template_id,0,'Feedback', NULL, NULL,0,@q_initial_question,'ASSESSMENT',1);
SET @f_initial_question=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_initial_question,'1,2',NULL,4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_initial_question,'3,4',NULL,3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_initial_question,'5,6',NULL,2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_initial_question,'7,8',NULL,1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_initial_question,'9,10',NULL,0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_initial_question, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_initial_question, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_initial_question, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_initial_question, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_initial_question, @word_count_validator);