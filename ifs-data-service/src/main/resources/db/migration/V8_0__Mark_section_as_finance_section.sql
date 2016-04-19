ALTER TABLE `section` ADD COLUMN finance TINYINT(1) DEFAULT 0;

UPDATE `section` SET `finance`=1 WHERE `id`='7';