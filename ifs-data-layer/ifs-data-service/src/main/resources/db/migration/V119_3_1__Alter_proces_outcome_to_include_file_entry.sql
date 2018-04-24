--IFS-3384 - include file upload to process outcome
ALTER TABLE process_outcome
        ADD COLUMN file_entry_id bigint(20),
        ADD CONSTRAINT `FK_process_outcome_file_entry_id` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`);
