ALTER TABLE file_entry
ADD COLUMN file_uuid VARCHAR(36) NULL AFTER name,
ADD COLUMN mdfive_checksum VARCHAR(255) NULL AFTER file_uuid,
ADD UNIQUE INDEX file_uuid_UNIQUE (file_uuid ASC);




