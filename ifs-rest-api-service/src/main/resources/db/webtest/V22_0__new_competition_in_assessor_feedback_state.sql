
/* in assessment */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`, `assessor_feedback_date`, `funders_panel_end_date`) VALUES (5,'2016-04-14','2016-01-12','Innovate UK is to invest up to £9 million in theremins. The aim of this competition is to make theremin the prominent instrument in music.','2016-03-16','Theremin Theory','2015-06-24',30,'2019-01-28','2016-01-28');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (49,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,5,NULL,'\0','GENERAL');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('118','100', '5', '5', '1', 'Application details');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('118',0, 1, 0, 1, 'Application details', 'Application details', 0, 0, '1', '5', '49');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('118', '118', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (118,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('119','100', '2', '5', '1', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('119',0, 1, 0, 2, 'Second Question', 'Secondly', 0, 0, '2', '5', '49');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('119', '119', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (119,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('120','100', '2', '5', '1', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('120',0, 1, 0, 3, 'Third Question', 'Thirdly', 0, 0, '3', '5', '49');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('120', '120', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (120,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('121','100', '2', '5', '1', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('121',0, 1, 0, 4, 'Fourth Question', 'Fourthly', 0, 0, '4', '5', '49');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('121', '121', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (121,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('122','100', '2', '5', '1', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('122',0, 1, 0, 5, 'Fifth Question', 'Fifthly', 0, 0, '5', '5', '49');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('122', '122', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (122,2);


INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (50,NULL,'Each partner must submit their finances','\0','Finances',2,5,NULL,0,'ORGANISATION_FINANCES');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (51,NULL,NULL,'\0','Your finances',3,5,50,1,'FINANCE');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (52,NULL,'This is the financial overview of all partners in the project','\0','Finances overview',4,5,50,1,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (53,NULL,NULL,'\0','Labour',1,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (54,NULL,NULL,'\0','Administration support costs',2,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (55,NULL,NULL,'\0','Materials',3,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (56,NULL,NULL,'\0','Capital usage',4,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (57,NULL,NULL,'\0','Subcontracting costs',5,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (58,NULL,NULL,'\0','Travel and subsistence',6,5,51,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (59,NULL,NULL,'\0','Other Costs',7,5,51,0,'GENERAL');

INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (123,1,1,1,'Provide the project costs for \'{organisationName}\'',NULL,0,0,15,NULL,5,51);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (124,1,0,1,NULL,NULL,0,0,1,NULL,5,53);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (125,1,0,1,NULL,NULL,0,0,1,NULL,5,54);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (126,1,0,1,NULL,NULL,0,0,1,NULL,5,55);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (127,1,0,1,NULL,NULL,0,0,1,NULL,5,56);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (128,1,0,1,NULL,NULL,0,0,1,NULL,5,57);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (129,1,0,1,NULL,NULL,0,0,1,NULL,5,58);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (130,1,1,1,'Labour',NULL,0,0,2,NULL,5,53);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (131,1,1,1,'Overheads',NULL,0,0,2,NULL,5,54);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (132,1,1,1,'Materials',NULL,0,0,2,NULL,5,55);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (133,1,1,1,'Capital Usage',NULL,0,0,2,NULL,5,56);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (134,1,1,1,'Sub-contracting costs',NULL,0,0,2,NULL,5,57);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (135,1,1,1,'Travel and subsistence',NULL,0,0,2,NULL,5,58);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (136,1,1,1,'Other Costs',NULL,0,0,2,NULL,5,59);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (137,1,1,1,'Other funding',NULL,0,0,20,NULL,5,51);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (138,1,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,0,0,17,NULL,5,52);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (139,0,1,1,'Funding level',NULL,0,0,19,NULL,5,51);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (140,1,1,1,'Organisation Size',NULL,0,0,18,NULL,5,51);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (141,0,0,0,NULL,NULL,0,0,16,NULL,5,52);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (142,0,0,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'',NULL,0,0,20,NULL,5,51);

INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (123,0,15,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (124,0,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (125,NULL,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (126,NULL,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (127,NULL,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (128,NULL,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (129,NULL,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (130,NULL,8,5,1,'Labour');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (131,NULL,9,5,1,'Overheads');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (132,NULL,10,5,1,'Materials');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (133,NULL,11,5,1,'Capital Usage');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (134,NULL,12,5,1,'Sub-contracting costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (135,NULL,13,5,1,'Travel and subsistence');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (136,NULL,14,5,1,'Other Costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (137,NULL,17,5,1,'Other funding');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (138,NULL,16,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (139,0,7,5,1,'Please enter the grant % you wish to claim for this project');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (140,NULL,19,5,1,'Organisation Size');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (141,0,6,5,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (142,0,20,5,0,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');

INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('123', '123', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('124', '124', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('125', '125', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('126', '126', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('127', '127', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('128', '128', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('129', '129', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('130', '130', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('131', '131', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('132', '132', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('133', '133', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('134', '134', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('135', '135', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('136', '136', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('137', '137', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('138', '138', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('139', '139', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('140', '140', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('141', '141', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('142', '142', '0');

INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (123,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (124,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (125,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (126,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (127,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (128,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (129,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (130,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (131,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (132,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (133,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (134,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (135,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (136,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (137,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (138,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (139,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (140,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (141,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (142,2);


