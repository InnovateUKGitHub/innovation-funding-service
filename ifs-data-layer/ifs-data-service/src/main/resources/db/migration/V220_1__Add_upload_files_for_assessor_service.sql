-- IFS-9619 - Assessment as a service PoC
-- Add a new table for upload files.

CREATE TABLE `upload_files` (
	`id` bigint(20) NOT NULL AUTO_INCREMENT,
	`type` varchar(255) DEFAULT NULL,
	`file_entry_id` bigint(20) DEFAULT NULL,
 	 PRIMARY KEY (`id`)
);
