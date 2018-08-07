-- IFS-3916 - Configurable Project Setup documents
-- Add a new table document_config_file_type

CREATE TABLE `document_config_file_type` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`document_config_id` bigint(20) NOT NULL,
	`file_type_id` bigint(20) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `document_config_fk` FOREIGN KEY (`document_config_id`) REFERENCES `document_config` (`id`),
	CONSTRAINT `file_type_fk` FOREIGN KEY (`file_type_id`) REFERENCES `file_type` (`id`)
);
