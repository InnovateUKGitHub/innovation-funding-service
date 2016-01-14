
INSERT INTO `question` (`assign_enabled`, `description`, `mark_as_completed_enabled`, `multiple_statuses`, `name`, `needing_assessor_feedback`, `needing_assessor_score`, `priority`, `competition_id`, `section_id`) VALUES (1, 'To determine the level of funding you are eligible to receive please provide your business sizing using the <a href="http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm" target="_blank" rel="external">EU Definition</a> for guidance.', 1, 1, 'Organisation Size', 0, 0, '17', '1', '7');
UPDATE `question` SET `priority`='18' WHERE `id`='38';
UPDATE `question` SET `priority`='19' WHERE `id`='35';
INSERT INTO `form_input` (`id`, `form_input_type_id`, `competition_id`, `included_in_application_summary`, `description`) VALUES ('40', '18', '1', '1', 'Organisation Size');
INSERT INTO `question_form_input` (`question_id`, `form_input_id`, `priority`) VALUES ('40', '40', '0');
INSERT INTO `form_input_type` (`id`, `title`) VALUES ('19', 'organisation_size');
UPDATE `form_input` SET `form_input_type_id`='19' WHERE `id`='40';


ALTER TABLE `application_finance` ADD COLUMN `organisation_size` VARCHAR(255) NULL DEFAULT NULL COMMENT '';


UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='1';
UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='2';
UPDATE `application_finance` SET `organisation_size`='LARGE' WHERE `id`='3';
UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='4';
UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='5';
UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='6';
UPDATE `application_finance` SET `organisation_size`='SMALL' WHERE `id`='7';
