--IFS-3384 - include file upload to process outcome
ALTER TABLE process_outcome
        ADD COLUMN file_entry_id bigint(20) DEFAULT NULL;