-- Alter invite table to use ENUM for status column
ALTER TABLE `invite`
    DROP FOREIGN KEY `invite_to_invite_status_fk`,
    DROP INDEX `invite_to_invite_status_fk`,
    CHANGE COLUMN `status` `status` ENUM('SENT', 'CREATED', 'OPENED') NOT NULL ;

-- Drop unused invite status table
DROP TABLE `invite_status`;