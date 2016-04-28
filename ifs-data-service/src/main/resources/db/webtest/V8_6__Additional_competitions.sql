
/* in assessment */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (2,'2016-12-31','2016-01-12','Innovate UK is to invest up to £9 million in juggling. The aim of this competition is to make juggling even more fun.','2016-03-16','Juggling Craziness','2015-06-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (16,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,2,NULL,'\0','GENERAL');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('43','100', '2', '2', '1', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('43',0, '<p>guidance</p>', '', 1, 0, 1, 'Project summary', 'Summary', 0, 0, '1', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('43', '43', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (43,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('44','100', '2', '2', '1', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('44',0, '<p>guidance</p>', '', 1, 0, 2, 'Second Question', 'Secondly', 0, 0, '2', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('44', '44', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (44,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('45','100', '2', '2', '1', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('45',0, '<p>guidance</p>', '', 1, 0, 3, 'Third Question', 'Thirdly', 0, 0, '3', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('45', '45', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (45,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('46','100', '2', '2', '1', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('46',0, '<p>guidance</p>', '', 1, 0, 4, 'Fourth Question', 'Fourthly', 0, 0, '4', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('46', '46', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (46,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('47','100', '2', '2', '1', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('47',0, '<p>guidance</p>', '', 1, 0, 5, 'Fifth Question', 'Fifthly', 0, 0, '5', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('47', '47', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (47,2);


INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (17,NULL,'Each partner must submit their finances','\0','Finances',2,2,NULL,0,'ORGANISATION_FINANCES');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (18,NULL,NULL,'\0','Your finances',3,2,17,1,'FINANCE');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (19,NULL,'This is the financial overview of all partners in the project','\0','Finances overview',4,2,17,1,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (20,NULL,NULL,'\0','Labour',1,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (21,NULL,NULL,'\0','Administration support costs',2,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (22,NULL,NULL,'\0','Materials',3,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (23,NULL,NULL,'\0','Capital usage',4,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (24,NULL,NULL,'\0','Subcontracting costs',5,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (25,NULL,NULL,'\0','Travel and subsistence',6,2,18,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (26,NULL,NULL,'\0','Other Costs',7,2,18,0,'GENERAL');

INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (48,1,'','',1,1,'Provide the project costs for \'{organisationName}\'',NULL,0,0,15,NULL,2,18);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (49,1,'<p>You can include the following labour costs, based upon your PAYE records:</p> <ul class=\"list-bullet\">         <li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li>     </ul><p>You can\'t include:</p><ul class=\"list-bullet\">         <li>discretionary bonuses</li><li>performance related payments of any kind</li></ul> <p>We base the total number of working days per year on full time days less standard holiday allowance. You should not include:</p><ul class=\"list-bullet\">         <li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul> <p>On the finance form, list the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost before we approve your application. The terms and conditions of the grant include compliance with these points.','Labour costs guidance',0,1,NULL,NULL,0,0,1,NULL,2,20);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (50,1,'Administration support costs guidance','',0,1,NULL,NULL,0,0,1,NULL,2,21);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (51,1,'If you are using materials supplied by associated companies or sub contracted from other consortium members then you are required to exclude the profit element of the value placed on that material - the materials should be charged at cost.\n\nSoftware that you have purchased specifically for use during your project should be included in materials.\n\nHowever if you already own software which will be used in the project, or it is provided for usage within your consortium by a consortium member, only additional costs incurred & paid between the start and end of your project will be eligible. Examples of costs that may be eligible are those related to the preparation of disks, manuals, installation, training or customisation.','Materials costs guidance',0,1,NULL,NULL,0,0,1,NULL,2,22);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (52,1,'Capital usage guidance','',0,1,NULL,NULL,0,0,1,NULL,2,23);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (53,1,'Subcontracting services supplied by associated companies should exclude any profit element and be charged at cost.  You should name the subcontractor (where known) and describe what the subcontractor will be doing and where the work will be undertaken. We will look at the size of this contribution when assessing eligibility and level of support. ','Subcontracting costs guidance',0,1,NULL,NULL,0,0,1,NULL,2,24);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (54,1,NULL,NULL,0,1,NULL,NULL,0,0,1,NULL,2,25);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (55,1,NULL,NULL,1,1,'Labour',NULL,0,0,2,NULL,2,20);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (56,1,NULL,NULL,1,1,'Overheads',NULL,0,0,2,NULL,2,21);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (57,1,NULL,NULL,1,1,'Materials',NULL,0,0,2,NULL,2,22);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (58,1,NULL,NULL,1,1,'Capital Usage',NULL,0,0,2,NULL,2,23);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (59,1,NULL,NULL,1,1,'Sub-contracting costs',NULL,0,0,2,NULL,2,24);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (60,1,NULL,NULL,1,1,'Travel and subsistence',NULL,0,0,2,NULL,2,25);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (61,1,NULL,NULL,1,1,'Other Costs',NULL,0,0,2,NULL,2,26);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (62,1,'Please tell us if you have received, or will receive any other public sector funding for this project.','What should I include in the other public funding section?',1,1,'Other funding',NULL,0,0,20,NULL,2,18);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (63,1,NULL,NULL,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,0,0,17,NULL,2,19);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (64,0,NULL,'What funding level should I enter?',1,1,'Funding level',NULL,0,0,19,NULL,2,18);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (65,1,NULL,NULL,1,1,'Organisation Size',NULL,0,0,18,NULL,2,18);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (66,0,NULL,NULL,0,0,NULL,NULL,0,0,16,NULL,2,19);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (67,0,NULL,'How do I create my Je-S output?',0,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'',NULL,0,0,20,NULL,2,18);

INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (48,0,15,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (49,0,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (50,NULL,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (51,NULL,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (52,NULL,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (53,NULL,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (54,NULL,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (55,NULL,8,2,1,'Labour');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (56,NULL,9,2,1,'Overheads');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (57,NULL,10,2,1,'Materials');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (58,NULL,11,2,1,'Capital Usage');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (59,NULL,12,2,1,'Sub-contracting costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (60,NULL,13,2,1,'Travel and subsistence');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (61,NULL,14,2,1,'Other Costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (62,NULL,17,2,1,'Other funding');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (63,NULL,16,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (64,0,7,2,1,'Please enter the grant % you wish to claim for this project');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (65,NULL,19,2,1,'Organisation Size');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (66,0,6,2,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (67,0,20,2,0,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');

INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('48', '48', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('49', '49', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('50', '50', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('51', '51', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('52', '52', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('53', '53', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('54', '54', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('55', '55', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('56', '56', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('57', '57', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('58', '58', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('59', '59', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('60', '60', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('61', '61', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('62', '62', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('63', '63', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('64', '64', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('65', '65', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('66', '66', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('67', '67', '0');

INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (48,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (49,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (50,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (51,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (52,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (53,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (54,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (55,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (56,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (57,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (58,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (59,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (60,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (61,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (62,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (63,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (64,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (65,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (66,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (67,2);


/* assessment over */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (3,'2016-04-14','2016-04-12','Innovate UK is to invest up to £9 million in cheese. The aim of this competition is to make cheese tastier.','2016-03-16','La Fromage','2015-06-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (27,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,3,NULL,'\0','GENERAL');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('68','100', '2', '3', '1', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('68',0, '<p>guidance</p>', '', 1, 0, 1, 'Project summary', 'Summary', 0, 0, '1', '3', '27');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('68', '68', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (68,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('69','100', '2', '3', '1', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('69',0, '<p>guidance</p>', '', 1, 0, 2, 'Second Question', 'Secondly', 0, 0, '2', '3', '27');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('69', '69', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (69,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('70','100', '2', '3', '1', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('70',0, '<p>guidance</p>', '', 1, 0, 3, 'Third Question', 'Thirdly', 0, 0, '3', '3', '27');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('70', '70', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (70,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('71','100', '2', '3', '1', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('71',0, '<p>guidance</p>', '', 1, 0, 4, 'Fourth Question', 'Fourthly', 0, 0, '4', '3', '27');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('71', '71', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (71,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('72','100', '2', '3', '1', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('72',0, '<p>guidance</p>', '', 1, 0, 5, 'Fifth Question', 'Fifthly', 0, 0, '5', '3', '27');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('72', '72', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (72,2);


INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (28,NULL,'Each partner must submit their finances','\0','Finances',2,3,NULL,0,'ORGANISATION_FINANCES');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (29,NULL,NULL,'\0','Your finances',3,3,28,1,'FINANCE');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (30,NULL,'This is the financial overview of all partners in the project','\0','Finances overview',4,3,28,1,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (31,NULL,NULL,'\0','Labour',1,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (32,NULL,NULL,'\0','Administration support costs',2,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (33,NULL,NULL,'\0','Materials',3,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (34,NULL,NULL,'\0','Capital usage',4,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (35,NULL,NULL,'\0','Subcontracting costs',5,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (36,NULL,NULL,'\0','Travel and subsistence',6,3,29,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (37,NULL,NULL,'\0','Other Costs',7,3,29,0,'GENERAL');

INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (73,1,'','',1,1,'Provide the project costs for \'{organisationName}\'',NULL,0,0,15,NULL,3,29);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (74,1,'<p>You can include the following labour costs, based upon your PAYE records:</p> <ul class=\"list-bullet\">         <li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li>     </ul><p>You can\'t include:</p><ul class=\"list-bullet\">         <li>discretionary bonuses</li><li>performance related payments of any kind</li></ul> <p>We base the total number of working days per year on full time days less standard holiday allowance. You should not include:</p><ul class=\"list-bullet\">         <li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul> <p>On the finance form, list the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost before we approve your application. The terms and conditions of the grant include compliance with these points.','Labour costs guidance',0,1,NULL,NULL,0,0,1,NULL,3,31);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (75,1,'Administration support costs guidance','',0,1,NULL,NULL,0,0,1,NULL,3,32);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (76,1,'If you are using materials supplied by associated companies or sub contracted from other consortium members then you are required to exclude the profit element of the value placed on that material - the materials should be charged at cost.\n\nSoftware that you have purchased specifically for use during your project should be included in materials.\n\nHowever if you already own software which will be used in the project, or it is provided for usage within your consortium by a consortium member, only additional costs incurred & paid between the start and end of your project will be eligible. Examples of costs that may be eligible are those related to the preparation of disks, manuals, installation, training or customisation.','Materials costs guidance',0,1,NULL,NULL,0,0,1,NULL,3,33);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (77,1,'Capital usage guidance','',0,1,NULL,NULL,0,0,1,NULL,3,34);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (78,1,'Subcontracting services supplied by associated companies should exclude any profit element and be charged at cost.  You should name the subcontractor (where known) and describe what the subcontractor will be doing and where the work will be undertaken. We will look at the size of this contribution when assessing eligibility and level of support. ','Subcontracting costs guidance',0,1,NULL,NULL,0,0,1,NULL,3,35);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (79,1,NULL,NULL,0,1,NULL,NULL,0,0,1,NULL,3,36);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (80,1,NULL,NULL,1,1,'Labour',NULL,0,0,2,NULL,3,31);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (81,1,NULL,NULL,1,1,'Overheads',NULL,0,0,2,NULL,3,32);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (82,1,NULL,NULL,1,1,'Materials',NULL,0,0,2,NULL,3,33);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (83,1,NULL,NULL,1,1,'Capital Usage',NULL,0,0,2,NULL,3,34);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (84,1,NULL,NULL,1,1,'Sub-contracting costs',NULL,0,0,2,NULL,3,35);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (85,1,NULL,NULL,1,1,'Travel and subsistence',NULL,0,0,2,NULL,3,36);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (86,1,NULL,NULL,1,1,'Other Costs',NULL,0,0,2,NULL,3,37);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (87,1,'Please tell us if you have received, or will receive any other public sector funding for this project.','What should I include in the other public funding section?',1,1,'Other funding',NULL,0,0,20,NULL,3,29);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (88,1,NULL,NULL,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,0,0,17,NULL,3,30);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (89,0,NULL,'What funding level should I enter?',1,1,'Funding level',NULL,0,0,19,NULL,3,29);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (90,1,NULL,NULL,1,1,'Organisation Size',NULL,0,0,18,NULL,3,29);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (91,0,NULL,NULL,0,0,NULL,NULL,0,0,16,NULL,3,30);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (92,0,NULL,'How do I create my Je-S output?',0,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'',NULL,0,0,20,NULL,3,29);

INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (73,0,15,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (74,0,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (75,NULL,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (76,NULL,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (77,NULL,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (78,NULL,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (79,NULL,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (80,NULL,8,3,1,'Labour');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (81,NULL,9,3,1,'Overheads');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (82,NULL,10,3,1,'Materials');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (83,NULL,11,3,1,'Capital Usage');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (84,NULL,12,3,1,'Sub-contracting costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (85,NULL,13,3,1,'Travel and subsistence');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (86,NULL,14,2,1,'Other Costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (87,NULL,17,3,1,'Other funding');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (88,NULL,16,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (89,0,7,3,1,'Please enter the grant % you wish to claim for this project');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (90,NULL,19,3,1,'Organisation Size');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (91,0,6,3,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (92,0,20,3,0,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');

INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('73', '73', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('74', '74', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('75', '75', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('76', '76', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('77', '77', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('78', '78', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('79', '79', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('80', '80', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('81', '81', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('82', '82', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('83', '83', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('84', '84', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('85', '85', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('86', '86', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('87', '87', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('88', '88', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('89', '89', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('90', '90', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('91', '91', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('92', '92', '0');

INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (73,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (74,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (75,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (76,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (77,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (78,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (79,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (80,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (81,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (82,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (83,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (84,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (85,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (86,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (87,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (88,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (89,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (90,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (91,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (92,2);

/* not started */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (4,'2018-12-31','2018-01-12','Innovate UK is to invest up to £9 million in sarcasm. The aim of this competition is to make sarcasm such a huge deal.','2018-03-16','Sarcasm Stupendousness','2018-02-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (38,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,4,NULL,'\0','GENERAL');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('93','100', '2', '4', '1', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('93',0, '<p>guidance</p>', '', 1, 0, 1, 'Project summary', 'Summary', 0, 0, '1', '4', '38');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('93', '93', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (93,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('94','100', '2', '4', '1', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('94',0, '<p>guidance</p>', '', 1, 0, 2, 'Second Question', 'Secondly', 0, 0, '2', '4', '38');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('94', '94', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (94,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('95','100', '2', '4', '1', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('95',0, '<p>guidance</p>', '', 1, 0, 3, 'Third Question', 'Thirdly', 0, 0, '3', '4', '38');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('95', '95', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (95,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('96','100', '2', '4', '1', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('96',0, '<p>guidance</p>', '', 1, 0, 4, 'Fourth Question', 'Fourthly', 0, 0, '4', '4', '38');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('96', '96', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (96,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('97','100', '2', '4', '1', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('97',0, '<p>guidance</p>', '', 1, 0, 5, 'Fifth Question', 'Fifthly', 0, 0, '5', '4', '38');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('97', '97', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (97,2);


INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (39,NULL,'Each partner must submit their finances','\0','Finances',2,4,NULL,0,'ORGANISATION_FINANCES');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (40,NULL,NULL,'\0','Your finances',3,4,39,1,'FINANCE');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (41,NULL,'This is the financial overview of all partners in the project','\0','Finances overview',4,4,39,1,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (42,NULL,NULL,'\0','Labour',1,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (43,NULL,NULL,'\0','Administration support costs',2,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (44,NULL,NULL,'\0','Materials',3,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (45,NULL,NULL,'\0','Capital usage',4,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (46,NULL,NULL,'\0','Subcontracting costs',5,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (47,NULL,NULL,'\0','Travel and subsistence',6,4,40,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (48,NULL,NULL,'\0','Other Costs',7,4,40,0,'GENERAL');

INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (98,1,'','',1,1,'Provide the project costs for \'{organisationName}\'',NULL,0,0,15,NULL,4,40);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (99,1,'<p>You can include the following labour costs, based upon your PAYE records:</p> <ul class=\"list-bullet\">         <li>gross salary</li><li>National Insurance</li><li>company pension contribution</li><li>life insurance</li><li>other non-discretionary package costs.</li>     </ul><p>You can\'t include:</p><ul class=\"list-bullet\">         <li>discretionary bonuses</li><li>performance related payments of any kind</li></ul> <p>We base the total number of working days per year on full time days less standard holiday allowance. You should not include:</p><ul class=\"list-bullet\">         <li>sick days</li><li>waiting time</li><li>training days</li><li>non-productive time</li></ul> <p>On the finance form, list the total days worked by all categories of staff on your project. Describe their role.</p><p>We will review the total amount of time and cost before we approve your application. The terms and conditions of the grant include compliance with these points.','Labour costs guidance',0,1,NULL,NULL,0,0,1,NULL,4,42);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (100,1,'Administration support costs guidance','',0,1,NULL,NULL,0,0,1,NULL,4,43);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (101,1,'If you are using materials supplied by associated companies or sub contracted from other consortium members then you are required to exclude the profit element of the value placed on that material - the materials should be charged at cost.\n\nSoftware that you have purchased specifically for use during your project should be included in materials.\n\nHowever if you already own software which will be used in the project, or it is provided for usage within your consortium by a consortium member, only additional costs incurred & paid between the start and end of your project will be eligible. Examples of costs that may be eligible are those related to the preparation of disks, manuals, installation, training or customisation.','Materials costs guidance',0,1,NULL,NULL,0,0,1,NULL,4,44);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (102,1,'Capital usage guidance','',0,1,NULL,NULL,0,0,1,NULL,4,45);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (103,1,'Subcontracting services supplied by associated companies should exclude any profit element and be charged at cost.  You should name the subcontractor (where known) and describe what the subcontractor will be doing and where the work will be undertaken. We will look at the size of this contribution when assessing eligibility and level of support. ','Subcontracting costs guidance',0,1,NULL,NULL,0,0,1,NULL,4,46);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (104,1,NULL,NULL,0,1,NULL,NULL,0,0,1,NULL,4,47);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (105,1,NULL,NULL,1,1,'Labour',NULL,0,0,2,NULL,4,42);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (106,1,NULL,NULL,1,1,'Overheads',NULL,0,0,2,NULL,4,43);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (107,1,NULL,NULL,1,1,'Materials',NULL,0,0,2,NULL,4,44);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (108,1,NULL,NULL,1,1,'Capital Usage',NULL,0,0,2,NULL,4,45);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (109,1,NULL,NULL,1,1,'Sub-contracting costs',NULL,0,0,2,NULL,4,46);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (110,1,NULL,NULL,1,1,'Travel and subsistence',NULL,0,0,2,NULL,4,47);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (111,1,NULL,NULL,1,1,'Other Costs',NULL,0,0,2,NULL,4,48);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (112,1,'Please tell us if you have received, or will receive any other public sector funding for this project.','What should I include in the other public funding section?',1,1,'Other funding',NULL,0,0,20,NULL,4,40);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (113,1,NULL,NULL,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,0,0,17,NULL,4,41);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (114,0,NULL,'What funding level should I enter?',1,1,'Funding level',NULL,0,0,19,NULL,4,40);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (115,1,NULL,NULL,1,1,'Organisation Size',NULL,0,0,18,NULL,4,40);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (116,0,NULL,NULL,0,0,NULL,NULL,0,0,16,NULL,4,41);
INSERT INTO `question` (`id`,`assign_enabled`,`guidance_answer`,`guidance_question`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (117,0,NULL,'How do I create my Je-S output?',0,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'',NULL,0,0,20,NULL,4,40);

INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (98,0,15,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (99,0,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (100,NULL,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (101,NULL,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (102,NULL,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (103,NULL,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (104,NULL,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (105,NULL,8,4,1,'Labour');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (106,NULL,9,4,1,'Overheads');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (107,NULL,10,4,1,'Materials');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (108,NULL,11,4,1,'Capital Usage');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (109,NULL,12,4,1,'Sub-contracting costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (110,NULL,13,4,1,'Travel and subsistence');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (111,NULL,14,2,1,'Other Costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (112,NULL,17,4,1,'Other funding');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (113,NULL,16,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (114,0,7,4,1,'Please enter the grant % you wish to claim for this project');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (115,NULL,19,4,1,'Organisation Size');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (116,0,6,4,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (117,0,20,4,0,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');

INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('98', '98', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('99', '99', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('100', '100', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('101', '101', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('102', '102', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('103', '103', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('104', '104', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('105', '105', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('106', '106', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('107', '107', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('108', '108', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('109', '109', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('110', '110', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('111', '111', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('112', '112', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('113', '113', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('114', '114', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('115', '115', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('116', '116', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('117', '117', '0');

INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (98,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (99,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (100,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (101,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (102,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (103,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (104,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (105,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (106,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (107,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (108,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (109,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (110,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (111,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (112,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (113,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (114,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (115,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (116,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (117,2);