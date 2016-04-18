
/* in assessment */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (2,'2016-12-31','2016-01-12','Innovate UK is to invest up to £9 million in juggling. The aim of this competition is to make juggling even more fun.','2016-03-16','Juggling Craziness','2015-06-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`) VALUES (16,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,2,NULL,'\0');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('43','10', '1', '2', '0', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('43',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('43', '43', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('44','10', '1', '2', '0', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('44',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('44', '44', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('45','10', '1', '2', '0', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('45',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('45', '45', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('46','10', '1', '2', '0', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('46',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('46', '46', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('47','10', '1', '2', '0', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('47',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '2', '16');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('47', '47', '0');

/* assessment over */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (3,'2016-04-14','2016-04-12','Innovate UK is to invest up to £9 million in cheese. The aim of this competition is to make cheese tastier.','2016-03-16','La Fromage','2015-06-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`) VALUES (17,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,3,NULL,'\0');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('48','10', '1', '3', '0', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('48',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '3', '17');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('48', '48', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('49','10', '1', '3', '0', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('49',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '3', '17');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('49', '49', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('50','10', '1', '3', '0', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('50',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '3', '17');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('50', '50', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('51','10', '1', '3', '0', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('51',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '3', '17');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('51', '51', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('52','10', '1', '3', '0', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('52',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '3', '17');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('52', '52', '0');

/* not started */
INSERT  IGNORE INTO `competition` (`id`, `assessment_end_date`, `assessment_start_date`, `description`, `end_date`, `name`, `start_date`, `max_research_ratio`) VALUES (4,'2018-12-31','2018-01-12','Innovate UK is to invest up to £9 million in sarcasm. The aim of this competition is to make sarcasm such a huge deal.','2018-03-16','Sarcasm Stupendousness','2018-02-24',30);
INSERT  IGNORE INTO `section` (`id`, `assessor_guidance_description`, `description`, `display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`) VALUES (18,NULL,'Please provide Innovate UK with information about your project. These sections are not scored but will provide background to the project.','\0','Project details',1,4,NULL,'\0');


INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('53','10', '1', '4', '0', 'First Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('53',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '4', '18');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('53', '53', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('54','10', '1', '4', '0', 'Second Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('54',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '4', '18');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('54', '54', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('55','10', '1', '4', '0', 'Third Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('55',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '4', '18');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('55', '55', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('56','10', '1', '4', '0', 'Fourth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('56',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '4', '18');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('56', '56', '0');

INSERT IGNORE INTO `form_input` (`id`,`word_count`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('57','10', '1', '4', '0', 'Fifth Question');
INSERT IGNORE INTO `question` (`id`,`assign_enabled`, `guidance_answer`, `guidance_question`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES ('57',0, '<p>guidance</p>', '', 0, 1, '', 0, 0, '20', '4', '18');
INSERT IGNORE INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('57', '57', '0');
