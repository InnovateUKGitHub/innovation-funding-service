-- IFS-9578: Add user name and date to the document approval banner
ALTER TABLE `project_document` ADD COLUMN `modified_by` BIGINT(20) DEFAULT NULL;
ALTER TABLE `project_document` ADD COLUMN `modified_date` datetime DEFAULT NULL;