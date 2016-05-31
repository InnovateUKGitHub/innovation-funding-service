
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`, `assessor_feedback_date`, `funders_panel_end_date`) VALUES (6,'2016-04-14','2016-01-12','Innovate UK is to invest up to £9 million in heavy rock music. The aim of this competition is to make it so whenever you turn on the radio, you hear killer riffs and sick breakdowns.','2016-03-16','Killer Riffs','2015-06-24',30,'2016-01-29','2016-01-28');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (60,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,6,NULL,'\0','GENERAL');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('143','100', '5', '6', '1', 'Application details');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('143',0, 1, 0, 1, 'Application details', 'Application details', 0, 0, '1', '6', '60');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('143', '143', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (143,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('144','100', '2', '6', '1', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('144',0, 1, 0, 2, 'Second Question', 'Secondly', 0, 0, '2', '6', '60');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('144', '144', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (144,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('145','100', '2', '6', '1', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('145',0, 1, 0, 3, 'Third Question', 'Thirdly', 0, 0, '3', '6', '60');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('145', '145', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (145,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('146','100', '2', '6', '1', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('146',0, 1, 0, 4, 'Fourth Question', 'Fourthly', 0, 0, '4', '6', '60');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('146', '146', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (146,2);

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('147','100', '2', '6', '1', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `mark_as_completed_enabled`, `multiple_statuses`, `question_number`, `name`, `short_name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('147',0, 1, 0, 5, 'Fifth Question', 'Fifthly', 0, 0, '5', '6', '60');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('147', '147', '0');
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (147,2);


INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (61,NULL,'Each partner must submit their finances','\0','Finances',2,6,NULL,0,'ORGANISATION_FINANCES');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (62,NULL,NULL,'\0','Your finances',3,6,61,1,'FINANCE');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (63,NULL,'This is the financial overview of all partners in the project','\0','Finances overview',4,6,61,1,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (64,NULL,NULL,'\0','Labour',1,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (65,NULL,NULL,'\0','Administration support costs',2,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (66,NULL,NULL,'\0','Materials',3,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (67,NULL,NULL,'\0','Capital usage',4,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (68,NULL,NULL,'\0','Subcontracting costs',5,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (69,NULL,NULL,'\0','Travel and subsistence',6,6,62,0,'GENERAL');
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (70,NULL,NULL,'\0','Other Costs',7,6,62,0,'GENERAL');

INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (148,1,1,1,'Provide the project costs for \'{organisationName}\'',NULL,0,0,15,NULL,6,62);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (149,1,0,1,NULL,NULL,0,0,1,NULL,6,64);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (150,1,0,1,NULL,NULL,0,0,1,NULL,6,65);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (151,1,0,1,NULL,NULL,0,0,1,NULL,6,66);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (152,1,0,1,NULL,NULL,0,0,1,NULL,6,67);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (153,1,0,1,NULL,NULL,0,0,1,NULL,6,68);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (154,1,0,1,NULL,NULL,0,0,1,NULL,6,69);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (155,1,1,1,'Labour',NULL,0,0,2,NULL,6,64);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (156,1,1,1,'Overheads',NULL,0,0,2,NULL,6,65);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (157,1,1,1,'Materials',NULL,0,0,2,NULL,6,66);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (158,1,1,1,'Capital Usage',NULL,0,0,2,NULL,6,67);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (159,1,1,1,'Sub-contracting costs',NULL,0,0,2,NULL,6,68);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (160,1,1,1,'Travel and subsistence',NULL,0,0,2,NULL,6,69);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (161,1,1,1,'Other Costs',NULL,0,0,2,NULL,6,70);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (162,1,1,1,'Other funding',NULL,0,0,20,NULL,6,62);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (163,1,0,0,'FINANCE_SUMMARY_INDICATOR_STRING',NULL,0,0,17,NULL,6,63);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (164,0,1,1,'Funding level',NULL,0,0,19,NULL,6,62);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (165,1,1,1,'Organisation Size',NULL,0,0,18,NULL,6,62);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (166,0,0,0,NULL,NULL,0,0,16,NULL,6,63);
INSERT INTO `question` (`id`,`assign_enabled`,`mark_as_completed_enabled`,`multiple_statuses`,`name`,`short_name`,`needing_assessor_feedback`,`needing_assessor_score`,`priority`,`question_number`,`competition_id`,`section_id`) VALUES (167,0,0,1,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'',NULL,0,0,20,NULL,6,62);

INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (148,0,15,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (149,0,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (150,NULL,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (151,NULL,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (152,NULL,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (153,NULL,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (154,NULL,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (155,NULL,8,6,1,'Labour');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (156,NULL,9,6,1,'Overheads');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (157,NULL,10,6,1,'Materials');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (158,NULL,11,6,1,'Capital Usage');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (159,NULL,12,6,1,'Sub-contracting costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (160,NULL,13,6,1,'Travel and subsistence');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (161,NULL,14,6,1,'Other Costs');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (162,NULL,17,6,1,'Other funding');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (163,NULL,16,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (164,0,7,6,1,'Please enter the grant % you wish to claim for this project');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (165,NULL,19,6,1,'Organisation Size');
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (166,0,6,6,1,NULL);
INSERT INTO `form_input` (`id`,`word_count`,`form_input_type_id`,`competition_id`,`included_in_application_summary`,`description`) VALUES (167,0,20,6,0,'Upload a pdf copy of the Je-S output confirming a status of \'With Council\'');

INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('148', '148', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('149', '149', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('150', '150', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('151', '151', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('152', '152', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('153', '153', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('154', '154', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('155', '155', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('156', '156', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('157', '157', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('158', '158', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('159', '159', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('160', '160', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('161', '161', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('162', '162', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('163', '163', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('164', '164', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('165', '165', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('166', '166', '0');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('167', '167', '0');

INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (148,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (149,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (150,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (151,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (152,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (153,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (154,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (155,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (156,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (157,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (158,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (159,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (160,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (161,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (162,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (163,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (164,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (165,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (166,2);
INSERT IGNORE INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES (167,2);


