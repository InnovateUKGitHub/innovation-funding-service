ALTER TABLE form_input_response ADD COLUMN file_entry_id bigint(20) DEFAULT NULL;
ALTER TABLE form_input_response ADD CONSTRAINT FOREIGN KEY(`file_entry_id`) REFERENCES `file_entry`(`id`);