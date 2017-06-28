
ALTER TABLE `section_template` DROP FOREIGN KEY `FK_st_ctid`;
ALTER TABLE `competition_template`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `section_template` ADD CONSTRAINT `FK_st_ctid` FOREIGN KEY (`competition_template_id`) REFERENCES `competition_template` (`id`);

ALTER TABLE `question_template` DROP FOREIGN KEY `FK_qqt_stid`;
ALTER TABLE `section_template` DROP FOREIGN KEY `FK_st_pstid`;
ALTER TABLE `section_template`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `question_template` ADD CONSTRAINT `FK_qqt_stid` FOREIGN KEY (`section_template_id`) REFERENCES `section_template` (`id`);
ALTER TABLE `section_template` ADD CONSTRAINT `FK_st_pstid` FOREIGN KEY (`parent_section_template_id`) REFERENCES `section_template` (`id`);

ALTER TABLE `question_template_form_input_template` DROP FOREIGN KEY `FK_qtfit_qtid`;
ALTER TABLE `question_template`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `question_template_form_input_template` ADD CONSTRAINT `FK_qtfit_qtid` FOREIGN KEY (`question_template_id`) REFERENCES `question_template` (`id`);

ALTER TABLE `form_input_template_form_validator` DROP FOREIGN KEY `FK_fitv_fitid`;
ALTER TABLE `question_template_form_input_template` DROP FOREIGN KEY `FK_qtfit_fitid`;
ALTER TABLE `form_input_template`
CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `form_input_template_form_validator` ADD CONSTRAINT `FK_fitv_fitid` FOREIGN KEY (`form_input_template_id`) REFERENCES `form_input_template` (`id`);
ALTER TABLE `question_template_form_input_template` ADD CONSTRAINT `FK_qtfit_fitid` FOREIGN KEY (`form_input_template_id`) REFERENCES `form_input_template` (`id`);