ALTER TABLE `form_input` ADD COLUMN `scope` varchar(255) DEFAULT NULL;
UPDATE `form_input` SET `scope`='APPLICATION' WHERE scope IS NULL;
ALTER TABLE `form_input` MODIFY `scope` varchar(255) NOT NULL;