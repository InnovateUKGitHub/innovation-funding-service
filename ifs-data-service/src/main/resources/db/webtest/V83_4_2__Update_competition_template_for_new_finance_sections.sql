
SET @programme_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Programme');
SET @sector_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Sector');
SET @programme_finance_parent_section_id=(SELECT id from section where competition_id=@programme_competition_template_id AND name='Your finances');
SET @sector_finance_parent_section_id=(SELECT id from section where competition_id=@sector_competition_template_id AND name='Your finances');

INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your project costs', '7', @programme_competition_template_id, @programme_finance_parent_section_id, 1, 'PROJECT_COST_FINANCES');
SET @programme_projectcost_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your organisation', '7', @programme_competition_template_id, @programme_finance_parent_section_id, 1, 'ORGANISATION_FINANCES');
SET @programme_organisation_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your funding', '7', @programme_competition_template_id, @programme_finance_parent_section_id, 1, 'FUNDING_FINANCES');
SET @programme_funding_fin_id=LAST_INSERT_ID();

INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your project costs', '7', @sector_competition_template_id, @sector_finance_parent_section_id, 1, 'PROJECT_COST_FINANCES');
SET @sector_projectcost_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your organisation', '7', @sector_competition_template_id, @sector_finance_parent_section_id, 1, 'ORGANISATION_FINANCES');
SET @sector_organisation_fin_id=LAST_INSERT_ID();
INSERT INTO `section` (`display_in_assessment_application_summary`, `name`, `priority`, `competition_id`, `parent_section_id`, `question_group`, `section_type`) VALUES (1, 'Your funding', '7', @sector_competition_template_id, @sector_finance_parent_section_id, 1, 'FUNDING_FINANCES');
SET @sector_funding_fin_id=LAST_INSERT_ID();

UPDATE `section` SET `section_type`='GENERAL' WHERE `name`='Finances' AND (competition_id=@programme_competition_template_id OR competition_id=@sector_competition_template_id);
UPDATE `section` SET `section_type`='OVERVIEW_FINANCES' WHERE `name`='Finances overview' AND (competition_id=@programme_competition_template_id OR competition_id=@sector_competition_template_id);

UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Labour' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Overhead costs' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Materials' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Capital usage' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Subcontracting costs' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Travel and subsistence' and `competition_id`=@programme_competition_template_id;
UPDATE `section` SET `parent_section_id`=@programme_projectcost_fin_id WHERE `name`='Other Costs' and `competition_id`=@programme_competition_template_id;

UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Labour' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Overhead costs' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Materials' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Capital usage' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Subcontracting costs' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Travel and subsistence' and `competition_id`=@sector_competition_template_id;
UPDATE `section` SET `parent_section_id`=@sector_projectcost_fin_id WHERE `name`='Other Costs' and `competition_id`=@sector_competition_template_id;

UPDATE `question` SET `section_id`=@programme_projectcost_fin_id WHERE `short_name`='Project finances' and `competition_id`=@programme_competition_template_id;
UPDATE `question` SET `section_id`=@sector_projectcost_fin_id WHERE `short_name`='Project finances' and `competition_id`=@sector_competition_template_id;

UPDATE `question` SET `section_id`=@programme_organisation_fin_id WHERE `short_name`='Business size' and `competition_id`=@programme_competition_template_id;
UPDATE `question` SET `section_id`=@sector_organisation_fin_id WHERE `short_name`='Business size' and `competition_id`=@sector_competition_template_id;

UPDATE `question` SET `section_id`=@programme_funding_fin_id WHERE `short_name`='Other funding' and `competition_id`=@programme_competition_template_id;
UPDATE `question` SET `section_id`=@programme_funding_fin_id WHERE `short_name`='Funding level' and `competition_id`=@programme_competition_template_id;
UPDATE `question` SET `section_id`=@programme_funding_fin_id WHERE `short_name`='Je-s Output' and `competition_id`=@programme_competition_template_id;
UPDATE `question` SET `section_id`=@sector_funding_fin_id WHERE `short_name`='Other funding' and `competition_id`=@sector_competition_template_id;
UPDATE `question` SET `section_id`=@sector_funding_fin_id WHERE `short_name`='Funding level' and `competition_id`=@sector_competition_template_id;
UPDATE `question` SET `section_id`=@sector_funding_fin_id WHERE `short_name`='Je-s Output' and `competition_id`=@sector_competition_template_id;