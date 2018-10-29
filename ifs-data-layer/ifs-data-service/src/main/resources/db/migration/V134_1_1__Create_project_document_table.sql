-- IFS-4458 - Project Documents - Table to capture documents (like Collaboration Agreement, Risk Register etc) during Project Setup
CREATE TABLE `project_document` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`project_id` BIGINT(20) NOT NULL,
	`document_config_id` BIGINT(20) NOT NULL,
	`file_entry_id` BIGINT(20) NOT NULL,
	`status` ENUM('UPLOADED','SUBMITTED', 'APPROVED', 'REJECTED') NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `UC_project_document` (`project_id`, `document_config_id`),
	CONSTRAINT `project_document_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
	CONSTRAINT `project_document_to_document_config_fk` FOREIGN KEY (`document_config_id`) REFERENCES `document_config` (`id`),
	CONSTRAINT `project_document_to_file_entry_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`)
);