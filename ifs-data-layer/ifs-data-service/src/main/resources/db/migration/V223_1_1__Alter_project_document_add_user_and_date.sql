-- IFS-9578: Add user name and date to the document approval banner
ALTER TABLE `project_document` ADD COLUMN `created_by` BIGINT(20) DEFAULT NULL;
ALTER TABLE `project_document` ADD COLUMN `created_date` datetime DEFAULT NULL;

ALTER TABLE `project_document` ADD CONSTRAINT `fk_entry_created_by` FOREIGN KEY(`created_by`) REFERENCES `user` (`id`);