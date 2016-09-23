ALTER TABLE `application` ADD COLUMN `resubmission` bit;
ALTER TABLE `application` ADD COLUMN `previous_application_number` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `application` ADD COLUMN `previous_application_title` VARCHAR(255) NULL DEFAULT NULL;
