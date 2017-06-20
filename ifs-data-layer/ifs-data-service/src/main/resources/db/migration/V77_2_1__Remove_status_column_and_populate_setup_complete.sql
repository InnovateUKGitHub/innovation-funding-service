ALTER TABLE `competition` DROP COLUMN `status`;

ALTER TABLE `competition` ADD `setup_complete` bit;

UPDATE `competition` SET `setup_complete`=1 WHERE `id`='1';
UPDATE `competition` SET `setup_complete`=1 WHERE `id`='2';
UPDATE `competition` SET `setup_complete`=1 WHERE `id`='3';
UPDATE `competition` SET `setup_complete`=1 WHERE `id`='4';
UPDATE `competition` SET `setup_complete`=1 WHERE `id`='5';
UPDATE `competition` SET `setup_complete`=1 WHERE `id`='6';
UPDATE `competition` SET `setup_complete`=0 WHERE `id`='7';