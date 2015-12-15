ALTER TABLE `section`
ADD COLUMN `question_group` BIT(1) NOT NULL AFTER `parent_section_id`;

UPDATE `section` SET `description`=NULL WHERE `id`='7';
UPDATE `section` SET `priority`='3', `question_group`=1 WHERE `id`='7';
UPDATE `section` SET `priority`='4', `question_group`=0 WHERE `id`='8';
UPDATE `section` SET `description`='Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section' WHERE `id`='6';

UPDATE `question` SET `description`='Only your organisation can see this level of detail. All members of your organisation can acesss and edit your finances We recommend assigning completion of your finances to one member of your team', `name`='Provide your organisation\'s finances' WHERE `id`='20';
DELETE FROM `question_form_input` WHERE `form_input_id`='19';
DELETE FROM `question_form_input` WHERE `form_input_id`='39';

DELETE FROM `form_input` WHERE `id`='19';
DELETE FROM `form_input` WHERE `id`='39';

DELETE FROM `question` WHERE `id`='19';
DELETE FROM `question` WHERE `id`='39';

