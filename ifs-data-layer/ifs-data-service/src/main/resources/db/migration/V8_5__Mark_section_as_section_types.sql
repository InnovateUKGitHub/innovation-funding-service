ALTER TABLE `section` DROP COLUMN finance;

ALTER TABLE `section` ADD COLUMN `section_type` VARCHAR(255) DEFAULT NULL;

UPDATE `section` SET `section_type`='ORGANISATION_FINANCES' WHERE `id`='6';
UPDATE `section` SET `section_type`='FINANCE' WHERE `id`='7';
UPDATE `section` SET `section_type`='GENERAL' WHERE `id` not in ('6','7');