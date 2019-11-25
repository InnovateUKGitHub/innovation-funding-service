-- IFS-6786 - grant_process_configuration - Table to capture which competitions projects should be sent to ACC
CREATE TABLE `grant_process_configuration` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`competition_id` BIGINT(20) NOT NULL,
	`send_by_default` tinyint(1) DEFAULT 0,
	PRIMARY KEY (`id`),
	CONSTRAINT `competition_id` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
);
