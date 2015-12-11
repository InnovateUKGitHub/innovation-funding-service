UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='1';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='2';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='3';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='4';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='5';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='6';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='7';
UPDATE `ifs`.`question` SET `section_id`='2' WHERE `id`='8';
UPDATE `ifs`.`question` SET `priority`='4', `section_id`='1' WHERE `id`='13';
UPDATE `ifs`.`question` SET `priority`='2' WHERE `id`='11';
UPDATE `ifs`.`question` SET `priority`='3' WHERE `id`='12';
UPDATE `ifs`.`question` SET `priority`='20', `section_id`='2' WHERE `id`='15';
UPDATE `ifs`.`question` SET `priority`='25', `section_id`='2' WHERE `id`='16';
UPDATE `ifs`.`question` SET `priority`='5' WHERE `id`='5';
UPDATE `ifs`.`question` SET `priority`='6' WHERE `id`='6';

UPDATE `ifs`.`section` SET `assessor_guidance_description`=NULL, `description`='Please provide Innovate UK with information about your project. These sections are not scored, but are required to provide background to the project.', `name`='Details' WHERE `id`='1';
UPDATE `ifs`.`section` SET `assessor_guidance_description`=NULL, `description`='These are the 10 questions which will be marked by assessors. Each question is marked out of 10 points.', `display_in_assessment_application_summary`=0, `name`='Application questions' WHERE `id`='2';
DELETE FROM `ifs`.`section` WHERE `id`='3';
DELETE FROM `ifs`.`section` WHERE `id`='4';
DELETE FROM `ifs`.`section` WHERE `id`='5';

