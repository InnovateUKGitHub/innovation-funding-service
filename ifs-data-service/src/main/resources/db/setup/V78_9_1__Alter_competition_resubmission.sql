ALTER TABLE `competition`
CHANGE COLUMN `resubmission` `resubmission` VARCHAR(255) NOT NULL DEFAULT 'UNSET' ;

UPDATE `competition` SET `resubmission` = 'UNSET' WHERE `resubmission` = 0;