ALTER TABLE `competition_type`
ADD COLUMN `is_active` TINYINT(1) NULL DEFAULT 1 AFTER `state_aid`;