ALTER TABLE `user`
ADD COLUMN `first_name` VARCHAR(255) NULL COMMENT '' AFTER `token`,
ADD COLUMN `invite_name` VARCHAR(255) NULL COMMENT '' AFTER `first_name`,
ADD COLUMN `last_name` VARCHAR(255) NULL COMMENT '' AFTER `invite_name`,
ADD COLUMN `phone_number` VARCHAR(255) NULL COMMENT '' AFTER `last_name`,
ADD COLUMN `title` VARCHAR(255) NULL COMMENT '' AFTER `phone_number`;