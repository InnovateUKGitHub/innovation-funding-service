--add to competition additional info
ALTER TABLE competition
ADD COLUMN `activity_code` VARCHAR(255) NULL AFTER `competition_type_id`,
ADD COLUMN `innovate_budget` VARCHAR(255) NULL AFTER `activity_code`,
ADD COLUMN `co_funders` VARCHAR(255) NULL AFTER `innovate_budget`,
ADD COLUMN `co_funders_budget` VARCHAR(255) NULL AFTER `co_funders`;