-- IFS-3916 - Configurable Project Setup documents
-- Add a new table project_document

CREATE TABLE `document_config` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`competition_id` bigint(20) NOT NULL,
	`title` varchar(255) NOT NULL,
	`guidance` varchar(5000) NOT NULL,
	`editable` bit(1) NOT NULL DEFAULT false,
	`enabled` bit(1) NOT NULL DEFAULT false,
	`type` varchar(31) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `document_config_to_competition_fk` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
);

CREATE TABLE `file_type` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`extension` varchar(255) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `name_uk` (`name`)
);

CREATE TABLE `document_config_file_type` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`document_config_id` bigint(20) NOT NULL,
	`file_type_id` bigint(20) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `document_config_fk` FOREIGN KEY (`document_config_id`) REFERENCES `document_config` (`id`),
	CONSTRAINT `file_type_fk` FOREIGN KEY (`file_type_id`) REFERENCES `file_type` (`id`)
);
