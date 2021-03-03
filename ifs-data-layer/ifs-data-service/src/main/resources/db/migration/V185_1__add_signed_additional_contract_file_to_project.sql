-- IFS-8737 upload signed annex

ALTER TABLE project ADD COLUMN signed_additional_contract_file_entry_id bigint(20);
ALTER TABLE project ADD CONSTRAINT `signed_additional_contract_file_entry_id_fk` FOREIGN KEY (`signed_additional_contract_file_entry_id`) REFERENCES `file_entry` (`id`);
