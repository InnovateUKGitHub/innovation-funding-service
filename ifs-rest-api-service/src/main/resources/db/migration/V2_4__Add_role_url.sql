ALTER TABLE `role` ADD COLUMN `url` VARCHAR(255) NULL DEFAULT NULL COMMENT '' AFTER `name`;
UPDATE `role` SET `url`='applicant/dashboard' WHERE `id`='1';
UPDATE `role` SET `url`='applicant/dashboard' WHERE `id`='2';
UPDATE `role` SET `url`='assessor/dashboard' WHERE `id`='3';
UPDATE `role` SET `url`='applicant/dashboard' WHERE `id`='4';
UPDATE `role` SET `url`='management/dashboard' WHERE `id`='5';
