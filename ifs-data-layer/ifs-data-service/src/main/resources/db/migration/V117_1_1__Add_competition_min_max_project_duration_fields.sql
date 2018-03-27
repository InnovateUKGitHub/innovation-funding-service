ALTER TABLE `competition`
ADD COLUMN `min_project_duration` INT NULL DEFAULT NULL AFTER `use_resubmission_question`,
ADD COLUMN `max_project_duration` INT NULL DEFAULT NULL AFTER `min_project_duration`;