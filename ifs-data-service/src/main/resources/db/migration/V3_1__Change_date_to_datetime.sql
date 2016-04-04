
ALTER TABLE `competition`
    CHANGE COLUMN `assessment_start_date` `assessment_start_date` DATETIME NULL DEFAULT NULL COMMENT '',
    CHANGE COLUMN `assessment_end_date` `assessment_end_date` DATETIME NULL DEFAULT NULL COMMENT '',
    CHANGE COLUMN `start_date` `start_date` DATETIME NULL DEFAULT NULL COMMENT '',
    CHANGE COLUMN `end_date` `end_date` DATETIME NULL DEFAULT NULL COMMENT '';