ALTER TABLE `competition_type`
ADD COLUMN `active` TINYINT(1) NULL DEFAULT 1 AFTER `state_aid`;