ALTER TABLE `application_finance`
ADD COLUMN `finance_file_entry_id` BIGINT(20) NULL AFTER `organisation_size`;
INSERT IGNORE INTO `form_input_type` (`id`,`title`) VALUES (20, 'finance_upload');
