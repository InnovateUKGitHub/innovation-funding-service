ALTER TABLE `organisation_type` ADD COLUMN `description` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `organisation_type` MODIFY COLUMN `description` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL AFTER `name`;
