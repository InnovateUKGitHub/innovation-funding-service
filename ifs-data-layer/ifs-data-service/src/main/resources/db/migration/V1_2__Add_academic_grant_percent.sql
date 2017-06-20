ALTER TABLE `competition`
ADD COLUMN `academic_grant_percentage` INT(11) NULL DEFAULT 0 AFTER `max_research_ratio`;
