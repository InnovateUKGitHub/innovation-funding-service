-- IFS-4494 - Create grant status table
-- Add a new table file_type

CREATE TABLE `grant_status` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`application_id` bigint(20) NOT NULL,
	`sent_requested` datetime NOT NULL,
	`sent_succeeded` datetime DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `application_id_uk` (`application_id`)
);