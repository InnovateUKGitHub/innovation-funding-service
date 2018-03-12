ALTER TABLE `competition`
ADD COLUMN `min_project_duration` INT NULL DEFAULT NULL AFTER `assessor_pay`,
ADD COLUMN `max_project_duration` INT NULL DEFAULT NULL AFTER `min_project_duration`;