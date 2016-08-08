ALTER TABLE `question` ADD COLUMN `question_type` VARCHAR(255) DEFAULT NULL;

UPDATE `question` SET `question_type`='GENERAL';
UPDATE `question` SET `question_type`='COST' WHERE `id` in ('28','30','31','32','33','34','35');
