CREATE TABLE `address_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `address_type`(id, name) VALUES (1, 'REGISTERED');
INSERT INTO `address_type`(id, name) VALUES (2, 'OPERATING');
INSERT INTO `address_type`(id, name) VALUES (3, 'PROJECT');

ALTER TABLE `organisation_address` ADD COLUMN `address_type_id` BIGINT(20) NOT NULL;

UPDATE `organisation_address`
SET `address_type_id` = CASE
  WHEN address_type = 'REGISTERED' THEN 1
  WHEN address_type = 'OPERATING' THEN 2
  WHEN address_type = 'PROJECT' THEN 3
  END;

ALTER TABLE `organisation_address` DROP COLUMN `address_type`;