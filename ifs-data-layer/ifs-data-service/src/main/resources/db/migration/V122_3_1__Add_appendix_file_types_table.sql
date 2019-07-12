--IFS-2959 Create a many-to-many relationship between form_input and FileTypeCategory for storing the allowed appendix's file types
CREATE TABLE `appendix_file_types` (
  `form_input_id` bigint(20) NOT NULL,
  `type` enum('PDF','SPREADSHEET') NOT NULL,
  PRIMARY KEY (`form_input_id`,`type`),
  CONSTRAINT `appendix_file_types_form_input_id_to_form_input_fk` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;