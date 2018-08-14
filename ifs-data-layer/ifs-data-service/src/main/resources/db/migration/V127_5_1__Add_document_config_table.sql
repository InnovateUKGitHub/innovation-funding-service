-- IFS-3916 - Configurable Project Setup documents
-- Add a new table document_config

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
