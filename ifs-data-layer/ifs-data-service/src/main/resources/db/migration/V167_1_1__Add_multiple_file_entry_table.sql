-- IFS-7309 - Create a many-to-many relationship between form_input_response and file_entry for storing multiple file entries
CREATE TABLE `form_input_file_entry` (
  `form_input_response_id` bigint(20) DEFAULT NULL,
  `file_entry_id` bigint(20) DEFAULT NULL
  PRIMARY KEY (`form_input_response_id`,`file_entry_id`),
  CONSTRAINT `form_input_file_entry_fk` FOREIGN KEY (`form_input_response_id`) REFERENCES `form_input_response` (`id`),
  CONSTRAINT `form_input_response_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;