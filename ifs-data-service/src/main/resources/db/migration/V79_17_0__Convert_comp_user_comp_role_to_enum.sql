ALTER TABLE `competition_user`
DROP FOREIGN KEY `competition_user_to_competition_role_fk`;
ALTER TABLE `competition_user`
DROP COLUMN `competition_role_id`,
ADD COLUMN `competition_role` ENUM('ASSESSOR') NULL DEFAULT NULL AFTER `competition_id`,
DROP INDEX `competition_user_to_competition_role_fk` ;
