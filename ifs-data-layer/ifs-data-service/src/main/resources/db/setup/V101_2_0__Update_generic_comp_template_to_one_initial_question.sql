/* Find the current the generic competition type and project details section ids */
SET @generic_template_id = 4;
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
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,1,0,'new','new',5,1,@generic_template_id,@project_details_questions_section,10,'GENERAL');
SET @q_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@generic_template_id,1,'1. What is the business opportunity that your project addresses?','What should I include in the business opportunity section?','<p>You should describe:</p><ul class=\"list-bullet\">         <li>the business opportunity you have identified and how you plan to take advantage of it</li><li>the customer needs you have identified and how your project will meet them</li><li>the challenges you expect to face and how you will overcome them</li></ul>',0,@q_business_opportunity,'APPLICATION',1);
SET @main_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@generic_template_id,0,'Question score',NULL,NULL,0,@q_business_opportunity,'ASSESSMENT',1);
SET @score_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@generic_template_id,1,'Appendix','What should I include in the appendix?','<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_business_opportunity,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@generic_template_id,0,'Feedback','Guidance for assessing business opportunity','Your score should be based upon the following:',0,@q_business_opportunity,'ASSESSMENT',1);
SET @f_business_opportunity=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'1,2','There is little or no business drive to the project. The results are not relevant to the target customers or no customer interests are provided.',4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'3,4','The business opportunity is unrealistic or poorly defined. The customer\'s true needs are not well understood and are not linked to the project\'s objectives.',3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'5,6','The business opportunity is plausible but not clearly expressed in terms of customer needs.',2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'7,8','The applicants have a good idea of the potential market and opportunities. The needs of the customer are central to the project\'s objectives.',1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'9,10','The applicants have a very clear understanding of the business opportunity and the problems that must be overcome to enable successful exploitation. The project is well aligned with these needs.',0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_business_opportunity, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_business_opportunity, @word_count_validator);