ALTER TABLE `application`
ADD COLUMN `completion` DECIMAL(5,2) NOT NULL DEFAULT 0 AFTER `assessor_feedback_file_entry_id`;