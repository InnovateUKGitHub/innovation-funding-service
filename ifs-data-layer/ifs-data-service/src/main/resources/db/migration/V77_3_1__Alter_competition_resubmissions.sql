ALTER TABLE `competition`
ADD COLUMN `resubmission` TINYINT(1) NOT NULL DEFAULT 0 AFTER `stream_name`;
