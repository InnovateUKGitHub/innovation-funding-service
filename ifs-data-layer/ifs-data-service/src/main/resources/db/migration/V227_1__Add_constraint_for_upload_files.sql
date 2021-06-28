-- (NOT USED NOW. NEED TO BE REMOVED IF NOT REQUIRED IN FUTURE)
-- IFS-9619 - Assessment as a service PoC
-- Adding constraint for uploading the files in the file entry table

ALTER TABLE upload_files ADD CONSTRAINT `file_entry_id_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`);
