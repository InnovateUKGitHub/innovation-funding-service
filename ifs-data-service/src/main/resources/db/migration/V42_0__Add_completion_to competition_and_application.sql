ALTER TABLE `application`
ADD COLUMN `completion` DOUBLE NOT NULL DEFAULT 0 AFTER `assessor_feedback_file_entry_id`;