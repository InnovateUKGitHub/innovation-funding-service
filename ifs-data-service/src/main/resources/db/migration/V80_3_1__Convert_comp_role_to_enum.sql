-- Alter competition_user table to have competition_role as ENUM
ALTER TABLE `competition_user`
    DROP FOREIGN KEY `competition_user_to_competition_role_fk`,
    DROP COLUMN `competition_role_id`,
    ADD COLUMN `competition_role` ENUM('ASSESSOR') NOT NULL AFTER `competition_id`,
    DROP INDEX `competition_user_to_competition_role_fk` ;

-- Drop unused competition_role table
DROP TABLE `competition_role`;

-- Convert competition_user competition_role fields to ENUM value
UPDATE `competition_user` SET `competition_role`='ASSESSOR'