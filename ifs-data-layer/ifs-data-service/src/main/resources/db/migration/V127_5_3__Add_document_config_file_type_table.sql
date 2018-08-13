-- IFS-3916 - Configurable Project Setup documents
-- Add a new table document_config_file_type

CREATE TABLE `document_config_file_type` (
	`document_config_id` bigint(20) NOT NULL,
	`file_type_id` bigint(20) NOT NULL,
	PRIMARY KEY (`document_config_id`,`file_type_id`),
	CONSTRAINT `document_config_fk` FOREIGN KEY (`document_config_id`) REFERENCES `document_config` (`id`),
	CONSTRAINT `file_type_fk` FOREIGN KEY (`file_type_id`) REFERENCES `file_type` (`id`)
);
