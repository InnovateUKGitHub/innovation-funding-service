-- Alter competition_user table to have competition_role as ENUM
ALTER TABLE `competition_user`
DROP FOREIGN KEY `competition_user_to_competition_role_fk`;
ALTER TABLE `competition_user`
DROP COLUMN `competition_role_id`,
ADD COLUMN `competition_role` ENUM('ASSESSOR') NULL DEFAULT NULL AFTER `competition_id`,
DROP INDEX `competition_user_to_competition_role_fk` ;

-- Drop unused competition_role table
DROP TABLE `competition_role`;

-- Convert competition_user competition_role fields to ENUM value
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='13';
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='14';
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='15';
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='16';
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='17';
UPDATE `competition_user` SET `competition_role`='ASSESSOR' WHERE `invite_id`='18';

-- Alter invite table to use ENUM for status column
ALTER TABLE `invite`
DROP FOREIGN KEY `invite_to_invite_status_fk`;
ALTER TABLE `invite`
DROP INDEX `invite_to_invite_status_fk` ;
ALTER TABLE `invite`
CHANGE COLUMN `status` `status` ENUM('SENT', 'CREATED', 'OPENED') NULL DEFAULT NULL ;

-- Drop unused invite status table
DROP TABLE `invite_status`;