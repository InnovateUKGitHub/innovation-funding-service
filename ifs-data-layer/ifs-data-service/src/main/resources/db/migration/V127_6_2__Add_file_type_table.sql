-- IFS-3916 - Configurable Project Setup documents
-- Add a new table file_type

CREATE TABLE `file_type` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`extension` varchar(255) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `name_uk` (`name`)
);
