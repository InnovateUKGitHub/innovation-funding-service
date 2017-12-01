/* Create the eoi competition type  */
INSERT INTO `competition_type` (`name`, `state_aid`, `active`, `template_competition_id`)
VALUES ('Expression of interest', 1, 1, NULL);
SET @competition_type_eoi_id=LAST_INSERT_ID();


SET @not_empty_validator = (SELECT `id` FROM form_validator WHERE `clazz_name` = 'org.innovateuk.ifs.validator.NotEmptyValidator');
SET @word_count_validator = (SELECT `id` FROM form_validator WHERE `clazz_name` = 'org.innovateuk.ifs.validator.WordCountValidator');

/* Create the eoi competition for template purpose  */
INSERT INTO `competition` (`name`,`max_research_ratio`,`academic_grant_percentage`,`budget_code`,`code`,`paf_code`,`executive_user_id`,`lead_technologist_user_id`,`competition_type_id`,`activity_code`,`innovate_budget`,`multi_stream`,`collaboration_level`,`stream_name`,`resubmission`,`setup_complete`,`full_application_finance`,`assessor_count`,`assessor_pay`,`template`,`use_resubmission_question`) VALUES ('Template for the Expression of interest competition type',0,100,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,1,0,0.00,1,1);
SET @eoi_template_id=LAST_INSERT_ID();
UPDATE competition_type SET template_competition_id = @eoi_template_id WHERE id = @competition_type_eoi_id;


/* Add the required section to the eoi competition template  */
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES ('These sections give important background information on the project. They do not need scoring however you do need to mark the scope.','Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.',1,'Project details',1,@eoi_template_id,NULL,0,'GENERAL');
SET @s_project_details=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES ('Each question should be given a score out of 10. Written feedback should also be given.','These are the 4 questions which will be marked by assessors. Each question is marked out of 10 points.',1,'Application questions',2,@eoi_template_id,NULL,0,'GENERAL');
SET @s_application_questions=LAST_INSERT_ID();

/* Project details questions */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'Enter the full title of the project',1,0,'Application details','Application details',1,NULL,@eoi_template_id,@s_project_details,NULL,'GENERAL');
SET @q_application_details=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,5,@eoi_template_id,1,'Application details',NULL,NULL,0,@q_application_details,'APPLICATION',1);

INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Please provide a short summary of your project. Make sure you include what is innovative about it.',1,0,'Project summary','Project summary',2,NULL,@eoi_template_id,@s_project_details,NULL,'GENERAL');
SET @q_project_summary=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'Project summary','What should I include in the project summary?','<p>We will not score this summary, but it will give the assessors a useful introduction to your project. It should provide a clear overview of the whole project, including:</p> <ul class=\"list-bullet\">         <li>your vision for the project</li><li>key objectives</li><li>main areas of focus</li><li>details of how it is innovative</li></ul>',0,@q_project_summary,'APPLICATION',1);
SET @main_project_summary=LAST_INSERT_ID();
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_project_summary, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_project_summary, @word_count_validator);

INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'If your application doesn\'t align with the scope, we will reject it.',1,0,'How does your project align with the scope of this competition?','Scope',4,NULL,@eoi_template_id,@s_project_details,NULL,'GENERAL');
SET @q_scope=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'How does your project align with the scope of this competition?','What should I include in the project scope?','<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this competition, you need to:</p><ul class=\"list-bullet\">         <li>read the competition brief in full</li><li>understand the background, challenge and scope of the competition</li><li>address the research objectives in your application</li><li>match your project\'s objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul> ',0,@q_scope,'APPLICATION',1);
SET @how_does_scope=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,21,@eoi_template_id,0,'Please select the research category for this project',NULL,NULL,0,@q_scope,'ASSESSMENT',1);
SET @please_select_scope=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,22,@eoi_template_id,0,'Is the application in scope?',NULL,NULL,0,@q_scope,'ASSESSMENT',1);
SET @is_the_scope=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@eoi_template_id,0,'Feedback','Guidance for assessing scope','Your answer should be based upon the following:',0,@q_scope,'ASSESSMENT',1);
SET @f_scope=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_scope,'NO','One or more of the above requirements have not been satisfied.',0);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_scope,'YES','The application contains the following:\nIs the consortia business led?\nAre there two or more partners to the collaboration?\nDoes it meet the scope of the competition as defined in the competition brief?',1);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@how_does_scope, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@please_select_scope, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@is_the_scope, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_scope, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@how_does_scope, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_scope, @word_count_validator);

/* Application questions
ACTUAL TEMPLATE QUESTIONS */
-- Business opportunity and potential market
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,1,0,'What is the business opportunity and potential market for your project?','Business opportunity and potential market',5,1,@eoi_template_id,@s_application_questions,10,'GENERAL');
SET @q_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'1. What is the business opportunity and potential market for your project?','What should I include in the business opportunity and potential market section?','<p>Describe:</p><ul class=\"list-bullet\"><li>the main motivation for your project: the business need or market opportunity</li><li>the domestic and international markets you will target, and the other markets you are considering targeting</li></ul>',0,@q_business_opportunity,'APPLICATION',1);
SET @main_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@eoi_template_id,0,'Question score',NULL,NULL,0,@q_business_opportunity,'ASSESSMENT',1);
SET @score_business_opportunity=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@eoi_template_id,1,'Appendix','What should I include in the appendix?','<p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_business_opportunity,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@eoi_template_id,0,'Feedback','Guidance for assessing market opportunity','Your score should be based upon the following:',0,@q_business_opportunity,'ASSESSMENT',1);
SET @f_business_opportunity=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'1,2','There is little or no business drive to the project. The market is not well defined or is wrong.',4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'3,4','The business opportunity is unrealistic or poorly defined. The market size is not well understood.',3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'5,6','The business opportunity is plausible and there is some understanding of the market.',2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'7,8','The applicants have a good idea of the potential business opportunity and market.',1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_business_opportunity,'9,10','The applicants understand the business opportunity. The market is well understood.',0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_business_opportunity, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_business_opportunity, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_business_opportunity, @word_count_validator);

-- Innovation
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Explain how your project is innovative in both a commercial and technical sense.',1,0,'What is innovative about your project?','Innovation',6,'2',@eoi_template_id,@s_application_questions,10,'GENERAL');
SET @q_potential_market=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'2. What is innovative about your project?','What should I include in the project innovation section?','<p>Describe:</p><ul class=\"list-bullet\">         <li>what the innovation will focus on</li><li>whether your project will apply existing technologies to new areas, develop new technologies for existing areas or use a totally disruptive approach</li><li>the freedom you have to operate</li></ul></ul>',0,@q_potential_market,'APPLICATION',1);
SET @main_potential_market=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@eoi_template_id,0,'Question score',NULL,NULL,0,@q_potential_market,'ASSESSMENT',1);
SET @score_potential_market=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@eoi_template_id,1,'Appendix','What should I include in the appendix?','<p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_potential_market,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@eoi_template_id,0,'Feedback','Guidance for assessing innovation','Your score should be based upon the following:',0,@q_potential_market,'ASSESSMENT',1);
SET @f_potential_market=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_potential_market,'1,2','The project is either not innovative or there is no exploitable route due to previous IP.',4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_potential_market,'3,4','The project lacks sufficient innovation both technically and commercially.',3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_potential_market,'5,6','The project is innovative and the technology relevant, but there is not enough confidence in the freedom to operate.',2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_potential_market,'7,8','The project will be innovative and relevant to the market. There is some understanding of the technology and confidence that there is freedom to operate.',1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_potential_market,'9,10','The project is significantly innovative either commercially or technically and will make a substantial contribution to the field. The technology is well understood and there is high confidence that there is freedom to operate.',0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_potential_market, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_potential_market, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_potential_market, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_potential_market, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_potential_market, @word_count_validator);


-- Project team
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Describe your ability to develop and exploit this technology. Include details of your team\'s track record in managing research and development projects.',1,0,'Who is in the project team and what are their roles?','Project team',7,'3',@eoi_template_id,@s_application_questions,10,'GENERAL');
SET @q_project_exploitation=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'3. Who is in the project team and what are their roles?','What should I include in the project team section?','<p>Describe or give:</p><ul class=\"list-bullet\">         <li>the roles, skills and relevant experience of all members of the project team</li><li>the resources, equipment and facilities required for your project, and how you will access them</li><li>details of any external parties, including sub-contractors, you will need</li></ul>',0,@q_project_exploitation,'APPLICATION',1);
SET @main_project_exploitation=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@eoi_template_id,0,'Question score',NULL,NULL,0,@q_project_exploitation,'ASSESSMENT',1);
SET @score_project_exploitation=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@eoi_template_id,1,'Appendix','What should I include in the appendix?','<p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_project_exploitation,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@eoi_template_id,0,'Feedback','Guidance for assessing team skills','Your score should be based upon the following:',0,@q_project_exploitation,'ASSESSMENT',1);
SET @f_project_exploitation=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_project_exploitation,'1,2','The consortium is not capable of either carrying out the project or exploiting the results.',4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_project_exploitation,'3,4','There are significant gaps in the consortium or the formation objectives are unclear. There could be some passengers or there is a poor balance between the work needed and the commitment shown.',3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_project_exploitation,'5,6','The consortium has most of the required skills and experience but there are a few gaps. The consortium will need to work hard to maintain a good working relationship.',2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_project_exploitation,'7,8','The consortium is strong and contains all the required skills and experience. The consortium is likely to work well.',1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_project_exploitation,'9,10','The consortium is ideally placed to carry out the project and exploit the results. The skills are well balanced and the partners likely to form a strong consortium with good knowledge transfer.',0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_project_exploitation, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_project_exploitation, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_project_exploitation, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_project_exploitation, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_project_exploitation, @word_count_validator);

-- Funding and adding value
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Estimate the total costs of the project and tell us how much funding you need from Innovate UK and why.',1,0,'How much will your project cost, and how does it represent value for money for your team and the taxpayer?','Funding and adding value',8,'4',@eoi_template_id,@s_application_questions,10,'GENERAL');
SET @q_economic_benefit=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (400,2,@eoi_template_id,1,'4. How much will your project cost, and how does it represent value for money for your team and the taxpayer?','What should I include in the funding and adding value section?','<p>Tell us:</p><ul class=\"list-bullet\"><li>what you estimate your project will cost in total</li><li>how your project’s goals justify the total project cost and the grant you are requesting</li><li>how your project represents value for money for you, and for the taxpayer</li><li>what you would spend your money on otherwise</li><li>whether your project could go ahead in any form without public funding, and if so, what difference the funding would make, such as speeding up the route to market, attracting more partners or reducing risk</li></ul>',0,@q_economic_benefit,'APPLICATION',1);
SET @main_economic_benefit=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,23,@eoi_template_id,0,'Question score',NULL,NULL,0,@q_economic_benefit,'ASSESSMENT',1);
SET @score_economic_benefit=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@eoi_template_id,1,'Appendix','What should I include in the appendix?','<p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_economic_benefit,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (100,2,@eoi_template_id,0,'Feedback','Guidance for assessing funding and adding value','Your score should be based upon the following:',0,@q_economic_benefit,'ASSESSMENT',1);
SET @f_economic_benefit=LAST_INSERT_ID();
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_economic_benefit,'1,2','The costs are not appropriate or justified. Any mix of research and development type is not justified. The work should be funded internally and does not deserve state funding.',4);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_economic_benefit,'3,4','The costs are either too high or too low to carry out the project. Poor justification is provided for any research and development type mix. There is not likely to be any improvement to the industrial partner\'s commitment to R&amp;D. The public funding won’t make much difference. The arguments for added value are poor or not sufficiently justified.',3);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_economic_benefit,'5,6','The project costs and justifications are broadly correct but there are some concerns. Any mix of research and development types is just about acceptable. The project will improve the industrial partners\' commitment to R&amp;D. The public funding will help. The arguments for added value are just about acceptable.',2);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_economic_benefit,'7,8','The project costs should be sufficient to successfully complete the project. Any mix of research and development types is justified and costed correctly. The project will increase the industrial partners\' commitment to R&amp;D. The public funding will make a difference. The arguments for added value are good and justified.',1);
INSERT INTO `guidance_row` (`form_input_id`,`subject`,`justification`,`priority`) VALUES (@f_economic_benefit,'9,10','The project costs are appropriate. Any mix of research and development types (such as industrial research with some work packages of experimental development) is justified and costed correctly. The project will significantly increase the industrial partners\' R&amp;D spend during the project and afterwards. The public funding will make a significant difference. The arguments for added value are very strong and believable.',0);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_economic_benefit, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@score_economic_benefit, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_economic_benefit, @not_empty_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@main_economic_benefit, @word_count_validator);
INSERT INTO `form_input_validator` (form_input_id, form_validator_id) VALUES(@f_economic_benefit, @word_count_validator);

/* No questions for finances section */
/* No questions for your finances section */

/* Assessor counts for this new competition type */
INSERT INTO `assessor_count_option` (`competition_type_id`, `option_name`, `option_value`, `default_option`)
VALUES
(@competition_type_eoi_id, '1', 1, 0),
(@competition_type_eoi_id, '3', 3, 0),
(@competition_type_eoi_id, '5', 5, 1);
