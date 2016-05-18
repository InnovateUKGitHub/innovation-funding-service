ALTER TABLE application ADD COLUMN assessor_feedback_file_entry_id BIGINT(20) NULL,
ADD CONSTRAINT `application_ibfk_1` FOREIGN KEY (`assessor_feedback_file_entry_id`)
REFERENCES `file_entry` (`id`);