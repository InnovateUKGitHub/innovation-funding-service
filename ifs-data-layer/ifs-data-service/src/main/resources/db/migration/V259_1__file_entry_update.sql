-- Add file_uuid column to file entry to support file-storage-service
-- Add UNIQUE index as db optimisation
ALTER TABLE `file_entry`
ADD COLUMN `file_uuid` VARCHAR(36) NULL AFTER `name`,
ADD UNIQUE INDEX `file_uuid_UNIQUE` (`file_uuid` ASC);