-- ADD OPEN INNOVATION_SECTOR
INSERT INTO `category` (`name`, `type`) VALUES ('Open', 'INNOVATION_SECTOR');
UPDATE `category` SET `id`=0 WHERE `name` ='Open' and `type` = 'INNOVATION_SECTOR';
