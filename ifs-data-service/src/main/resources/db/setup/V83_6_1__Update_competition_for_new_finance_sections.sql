-- Insert the new finance sub sections
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your project costs', '7', '1', '7', 1, 'PROJECT_COST_FINANCES');
SET @projectcost_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your organisation', '7', '1', '7', 1, 'ORGANISATION_FINANCES');
SET @organisation_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your funding', '7', '1', '7', 1, 'FUNDING_FINANCES');
SET @funding_fin_id=LAST_INSERT_ID();

-- Update the section types for the new enum values.
UPDATE `section` SET `section_type`='GENERAL' WHERE `id`='6';
UPDATE `section` SET `section_type`='OVERVIEW_FINANCES' WHERE `id`='8';

-- Update the priorities types for the new sections.
UPDATE `section` SET `priority`=3 WHERE `id`='6';
UPDATE `section` SET `priority`=4 WHERE `id`='7';
UPDATE `section` SET `priority`=5 WHERE `id`=@projectcost_fin_id;
UPDATE `section` SET `priority`=6 WHERE `id`=@organisation_fin_id;
UPDATE `section` SET `priority`=7 WHERE `id`=@funding_fin_id;
UPDATE `section` SET `priority`=8 WHERE `id`='8';

-- Update existing sections to belong to new finance subsections.
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='9';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='10';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='11';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='12';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='13';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='14';
UPDATE `section` SET `parent_section_id`=@projectcost_fin_id WHERE `id`='15';

-- Update questions to exist in new finance subsections.
UPDATE `question` SET `section_id`=@projectcost_fin_id WHERE `id`='20';
UPDATE `question` SET `section_id`=@organisation_fin_id WHERE `id`='40';
UPDATE `question` SET `section_id`=@funding_fin_id WHERE `id`='35';
UPDATE `question` SET `section_id`=@funding_fin_id WHERE `id`='38';
UPDATE `question` SET `section_id`=@funding_fin_id WHERE `id`='42';


