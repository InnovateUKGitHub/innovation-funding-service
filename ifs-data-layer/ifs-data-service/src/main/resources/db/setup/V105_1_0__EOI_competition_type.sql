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
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES ('Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section','Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section',1,'Finances',3,@eoi_template_id,NULL,0,'GENERAL');
SET @s_finances=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Your finances',4,@eoi_template_id,@s_finances,1,'FINANCE');
SET @s_your_finances=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,1,'Your project costs',5,@eoi_template_id,@s_your_finances,1,'PROJECT_COST_FINANCES');
SET @s_your_project_costs=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,1,'Your organisation',6,@eoi_template_id,@s_your_finances,1,'ORGANISATION_FINANCES');
SET @s_your_organisation=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,1,'Your funding',7,@eoi_template_id,@s_your_finances,1,'FUNDING_FINANCES');
SET @s_your_funding=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Labour',1,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_labour=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Overhead costs',2,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_overheads=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Materials',3,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_materials=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Capital usage',4,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_capital_usage=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Subcontracting costs',5,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_subcontracting=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Travel and subsistence',6,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_travel=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,NULL,0,'Other costs',7,@eoi_template_id,@s_your_project_costs,0,'GENERAL');
SET @s_other_costs=LAST_INSERT_ID();
INSERT INTO `section` (`assessor_guidance_description`,`description`,`display_in_assessment_application_summary`,`name`,`priority`,`competition_id`,`parent_section_id`,`question_group`,`section_type`) VALUES (NULL,'This is the financial overview of all partners in this collaboration. Each partner should submit their organisation\'s finances in the \'your finances\' section. All partners will see this level of detail.',1,'Finances overview',8,@eoi_template_id,@s_finances,1,'OVERVIEW_FINANCES');
SET @s_finance_overview=LAST_INSERT_ID();


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

/* Your project costs question */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'Only your organisation can see this level of detail. All members of your organisation can access and edit your finances. We recommend assigning completion of your finances to one member of your team. <a href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance\" rel=\"external\">Find out which project costs are eligible.</a> ',1,1,'Provide the project costs for \'{organisationName}\'','Project finances',15,NULL,@eoi_template_id,@s_your_project_costs,NULL,'GENERAL');
SET @q_your_project_costs=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,15,@eoi_template_id,1,NULL,'','',0,@q_your_project_costs,'APPLICATION',1);

/* Your organisation question */
SET @staff_count_id = (SELECT id FROM form_input_type WHERE `name` =  'STAFF_COUNT');
SET @staff_turnover_id = (SELECT id FROM form_input_type WHERE `name` =  'ORGANISATION_TURNOVER');
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'To determine the level of funding you are eligible to receive please provide your business size using the <a href=\"http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm\" target=\"_blank\" rel=\"external\">EU Definition</a> for guidance.',1,1,'Organisation size','Business size',18,NULL,@eoi_template_id,@s_your_organisation,NULL,'GENERAL');
SET @q_your_organisation=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,19,@eoi_template_id,1,'Organisation Size',NULL,NULL,0,@q_your_organisation,'APPLICATION',1);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,4,@eoi_template_id,1,'Appendix','What should I include in the appendix?','<p>You may include an appendix of additional information to support the technical approach the project will undertake.</p><p>You may include, for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul class=\"list-bullet\"><li>be in a portable document format (.pdf)</li><li>be readable with 100% magnification</li><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li><li>be less than 1mb in size</li></ul>',1,@q_your_organisation,'APPLICATION',0);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,@staff_turnover_id,@eoi_template_id,0,'Turnover (£)','Your turnover from the last financial year.','',1,@q_your_organisation,'APPLICATION',1);
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,@staff_count_id,@eoi_template_id,0,'Full time employees','Number of full time employees at your organisation.','',2,@q_your_organisation,'APPLICATION',1);

/* Add a form input under the organisation size question for all competitions and templates. Default is inactive.
 Financial Year End. Get the form input type and then do an insert for every organisation size question. */
SET @financial_year_end_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_YEAR_END');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_year_end_id, q.competition_id, false, "End of last financial year", "Enter the date of your last financial year.", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;

/* Financial Overview rows. */
SET @financial_overview_row_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_OVERVIEW_ROW');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual turnover", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual profits", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual export", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Research and development spend", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;

/* Full time employees at year end */
SET @financial_staff_count_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_STAFF_COUNT');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_staff_count_id, q.competition_id, false, "Full time employees", "How many full-time employees did you have on the project at the close of your last financial year?", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.id = @q_your_organisation;

/* Connect the validator to the form inputs */
SET @past_month_validator_id = (SELECT id FROM form_validator WHERE title = 'PastMMYYYYValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @past_month_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_year_end_id)
    AND fi.competition_id=@eoi_template_id;

SET @integer_validator_id = (SELECT id FROM form_validator WHERE title = 'SignedLongIntegerValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @integer_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_overview_row_id)
    AND fi.competition_id=@eoi_template_id;

/* Connect the validator to the form inputs */
SET @non_negative_integer_validator_id = (SELECT id FROM form_validator WHERE title = 'NonNegativeLongIntegerValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @non_negative_integer_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_staff_count_id, @staff_count_id, @staff_turnover_id)
    AND fi.competition_id=@eoi_template_id;

SET @financial_year_end_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_YEAR_END');
SET @financial_overview_row_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_OVERVIEW_ROW');
SET @financial_staff_count_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_STAFF_COUNT');

/*  Currently the only competition of type sector is the template - set active to false for all of the relevant
form inputs then change to active for the sector template. */
UPDATE form_input SET active = false
 WHERE competition_id=@eoi_template_id
   AND (form_input_type_id=@financial_year_end_type_id OR form_input_type_id=@financial_overview_row_type_id OR form_input_type_id=@financial_staff_count_type_id);


/* Your funding question */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'Please tell us if you have ever applied for or received any other public sector funding for this project. You should also include details of any offers of funding you\'ve received.',1,1,'Other funding','Other funding',20,NULL,@eoi_template_id,@s_your_funding,NULL,'COST');
SET @q_other_funding=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,17,@eoi_template_id,1,'Other funding','What should I include in the other public funding section?','<p>You must provide details of other public funding that you are currently applying for (or have already applied for) in relation to this project. You do not need to include completed grants that were used to reach this point in the development process. This information is important as other public sector support counts as part of the funding you can receive for your project.</p>',0,@q_other_funding,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'Please enter the funding level that you would like to apply for in this application',1,1,'Funding level','Funding level',19,NULL,@eoi_template_id,@s_your_funding,NULL,'GENERAL');
SET @q_funding_level=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,7,@eoi_template_id,1,'Please enter the grant % you wish to claim for this project','What funding level should I enter?','<p>For a business or academic organisation, you can apply for any funding percentage between 0% and the maximum allowable for your organisation size. For other organisation types, you can apply for any funding percentage between 0% and 100%. The amount you apply for must reflect other funding you may have received. It must also be within participation levels which you can review on the Finances Overview page.</p>',0,@q_funding_level,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,NULL,1,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'','Je-s Output',20,NULL,@eoi_template_id,@s_your_funding,NULL,'GENERAL');
SET @q_jes_output=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,20,@eoi_template_id,1,'Upload a pdf copy of the Je-S output form once you have a status of \'With Council\'.','How do I create my Je-S output?','<p>You should include only supporting information in the appendix. You shouldn’t use it to provide your responses to the question.</p><p>Guidance for this section needs to be created</p>',0,@q_jes_output,'APPLICATION',1);

/* Labour question */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You may claim the labour costs of all individuals you have working on your project.</p> <p> If your application is awarded funding, you will need to account for all your labour costs as they occur. For example, you should keep timesheets and payroll records. These should show the actual hours worked by individuals and paid by the organisation.</p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_labour,NULL,'GENERAL');
SET @q_labour_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,'Labour costs guidance','<p>You can include the following labour costs, based upon your PAYE records:</p><ul class=\"list-bullet\"><li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li></ul><p>You can\'t include:</p><ul class=\"list-bullet\"><li>discretionary bonuses</li><li>performance related payments of any kind</li></ul><p>You may include the total number of working days for staff but do not include:</p><ul class=\"list-bullet\"><li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul><p>Enter the total number of working days in the year. List the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost of labour before we approve your application. The terms and conditions of the grant include compliance with these points.</p>',0,@q_labour_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,1,1,'Labour',NULL,2,NULL,@eoi_template_id,@s_labour,NULL,'COST');
SET @q_labour_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,8,@eoi_template_id,1,'Labour',NULL,NULL,0,@q_labour_cost,'APPLICATION',1);

/* Overheads question */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_overheads,NULL,'GENERAL');
SET @q_overhead_empty=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,NULL,NULL,0,@q_overhead_empty,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You may incur indirect support staff costs linked with your administrative work for the project. To be eligible, these costs should be directly attributable and incremental to the project. Indirect costs associated with commercial activities are not eligible and must not be included. For further information on which costs are eligible please read our <a href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance\">project costs guidance</a>.</p>',1,1,'Overheads','Indirect costs',2,NULL,@eoi_template_id,@s_overheads,NULL,'GENERAL');
SET @q_overhead=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,9,@eoi_template_id,1,'Overheads',NULL,NULL,0,@q_overhead,'APPLICATION',1);

/* Materials question */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You can claim the costs of materials used on your project providing:</p><ul class=\"list-bullet\"><li>they are not already purchased or included in the administration support costs</li><li>they are purchased from third parties</li><li>they won’t have a residual/resale value at the end of your project. If they do you can claim the costs minus this value</li></ul><p><a href=\"https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance\" rel=\"external\">Please refer to our guide to project costs for further information.</a></p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_materials,NULL,'GENERAL');
SET @q_materials_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,'Materials costs guidance','<p>Materials supplied by associated companies or project partners should be charged at cost.</p><p>Software that you have purchased specifically for use during your project may be included. If you already own the software then only additional costs which are incurred and paid during your project, will be eligible. For example, installation, training or customisation.</p><p>Material costs must be itemised to justify that they are eligible.</p>',0,@q_materials_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Please provide a breakdown of the materials you expect to use during the project',1,1,'Materials','Materials',2,NULL,@eoi_template_id,@s_materials,NULL,'COST');
SET @q_materials_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,10,@eoi_template_id,1,'Materials',NULL,NULL,0,@q_materials_cost,'APPLICATION',1);

/* Capital usage */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You should provide details of capital equipment and tools you will buy for, or use on, your project.</p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_capital_usage,NULL,'GENERAL');
SET @q_capital_usage_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,'Capital usage guidance','<p>You will need to calculate a ‘usage’ value for each item. You can do this by deducting its expected value from its original price at the end of your project. If you owned the equipment before the project started then you should use its Net Present Value.</p><p>This value is then multiplied by the amount, in percentages, that is used during the project. This final value represents the eligible cost to your project.</p>',0,@q_capital_usage_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Please provide a breakdown of the capital items you will buy and/or use for the project.',1,1,'Capital Usage','Capital items',2,NULL,@eoi_template_id,@s_capital_usage,NULL,'COST');
SET @q_capital_usage_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,11,@eoi_template_id,1,'Capital Usage',NULL,NULL,0,@q_capital_usage_cost,'APPLICATION',1);

/* Subcontracting costs */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You may subcontract work if you don’t have the expertise in your consortium. You can also subcontract if it is cheaper than developing your skills in-house.</p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_subcontracting,NULL,'GENERAL');
SET @q_subcontracting_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,'Subcontracting costs guidance','<p>Subcontracting costs relate to work carried out by third party organisations. These organisations are not part of your project or collaboration.</p><p>Subcontracting is eligible providing it’s justified as to why the work cannot be performed by a project partner.</p><p>Where possible you should select a UK based contractor. You should name the subcontractor (where known) and describe what they will be doing. You should also state where the work will be undertaken. We will look at the size of this contribution when assessing your eligibility and level of support.</p>',0,@q_subcontracting_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Please provide details of any work that you expect to subcontract for your project.',1,1,'Sub-contracting costs','Sub-contracts',2,NULL,@eoi_template_id,@s_subcontracting,NULL,'COST');
SET @q_subcontracting_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,12,@eoi_template_id,1,'Sub-contracting costs',NULL,NULL,0,@q_subcontracting_cost,'APPLICATION',1);

/* Travel costs */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>You should include travel and subsistence costs that relate only to this project. </p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_travel,NULL,'GENERAL');
SET @q_travel_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,NULL,NULL,0,@q_travel_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,1,1,'Travel and subsistence',NULL,2,NULL,@eoi_template_id,@s_travel,NULL,'COST');
SET @q_travel_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,13,@eoi_template_id,1,'Travel and subsistence',NULL,NULL,0,@q_travel_cost,'APPLICATION',1);

/* Other costs */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'<p>Please tell us if you have applied for or received any other public sector funding for this project. Any other funding received will need to be deducted from the total value of any funding you are claiming for this project.</p>',0,1,NULL,NULL,1,NULL,@eoi_template_id,@s_other_costs,NULL,'GENERAL');
SET @q_other_content=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,'Other costs guidance','<p>Examples of other costs include:</p><p><strong>Training costs:</strong> these costs are eligible for support if they relate to your project. We may support management training for your project but will not support ongoing training.</p><p><strong>Preparation of technical reports:</strong> if, for example, the main aim of your project is standard support or technology transfer. You should show how this is more than you would produce through good project management.</p><p><strong>Market assessment:</strong> we may support market assessments studies. The study will need to help us understand how your project is a good match for your target market. It could also be eligible if it helps commercialise your product.</p><p><strong>Licensing in new technologies:</strong> if new technology makes up a large part of your project, we will expect you to develop that technology. For instance, if the value of the technology is more than &pound;100,000.</p><p><strong>Patent filing costs for New IP generated by your project:</strong> these are eligible for SMEs up to a limit of &pound;7,500 per partner. You should not include legal costs relating to the filing or trademark related expenditure.</p><p>Regulatory compliance costs are eligible if necessary to carry out your project.</p>',0,@q_other_content,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,'Please note that legal or project audit and accountancy fees are not eligible and should not be included as an \'other cost’. Patent filing costs of New IP relating to the project are limited to £7,500 for SME applicants only.  Please provide estimates of other costs that do not fit within any other cost headings.',1,1,'Other costs','Other costs',2,NULL,@eoi_template_id,@s_other_costs,NULL,'COST');
SET @q_other_cost=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,14,@eoi_template_id,1,'Other costs',NULL,NULL,0,@q_other_cost,'APPLICATION',1);


/* Finance overview questions */
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (1,NULL,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,17,NULL,@eoi_template_id,@s_finance_overview,NULL,'GENERAL');
SET @q_finance_summary_indicator=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,16,@eoi_template_id,1,NULL,NULL,NULL,0,@q_finance_summary_indicator,'APPLICATION',1);
INSERT INTO `question` (`assign_enabled`,`description`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`priority`,`question_number`,`competition_id`,`section_id`,`assessor_maximum_score`,`question_type`) VALUES (0,'<h2 class=\"heading-medium\">Funding rules for this competition</h2><p>We will fund projects between &pound;500,000 and &pound;1.5 million. We may consider projects with costs outside of this range. We expect projects to last between 12 and 36 months.</p><p>Innovate UK\'s aim is to optimise the level of funding businesses receive. We also recognise the importance of research organisations\' contribution to R&amp;D projects. Therefore we require the following levels of participation:</p><ul class=\"list-bullet\"> <li>at least 70% of the total eligible project costs are incurred by commercial organisations and</li> <li>a maximum of 30% of total eligible project costs are available to research participants. Where there is more than one research participant, this maximum will be shared between them.</li></ul>',0,0,NULL,NULL,16,NULL,@eoi_template_id,@s_finance_overview,NULL,'GENERAL');
SET @q_funding_rules=LAST_INSERT_ID();
INSERT INTO `form_input` (`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`,`guidance_title`,`guidance_answer`,`priority`,`question_id`,`scope`,`active`) VALUES (NULL,6,@eoi_template_id,1,NULL,NULL,NULL,0,@q_funding_rules,'APPLICATION',1);

/* Assessor counts for this new competition type */
INSERT INTO `assessor_count_option` (`competition_type_id`, `option_name`, `option_value`, `default_option`)
VALUES
(@competition_type_eoi_id, '1', 0, 0),
(@competition_type_eoi_id, '3', 0, 0),
(@competition_type_eoi_id, '5', 0, 1);
