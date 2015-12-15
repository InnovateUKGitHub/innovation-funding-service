DROP TABLE IF EXISTS `process_outcome`;

CREATE TABLE `process_outcome` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `outcome` varchar(255) DEFAULT NULL,
  `outcome_type` varchar(255) DEFAULT NULL,
  `process_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rm72g2d5hsse93bn54jimfkbw` (`process_id`),
  CONSTRAINT `FK_rm72g2d5hsse93bn54jimfkbw` FOREIGN KEY (`process_id`) REFERENCES `process` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `process_outcome` (`id`, `description`, `outcome`, `outcome_type`, `process_id`) VALUES (1, 'hey', 'YES', 'assessment', '1');
INSERT IGNORE INTO `process_outcome` (`id`, `outcome`, `outcome_type`, `process_id`) VALUES (2, 'YES', 'assessment', '2');
INSERT IGNORE INTO `process_outcome` (`id`, `outcome`, `outcome_type`, `process_id`) VALUES (3, 'YES', 'assessment', '3');
INSERT IGNORE INTO `process_outcome` (`id`, `outcome`, `outcome_type`, `process_id`) VALUES (4, 'YES', 'assessment', '7');

ALTER TABLE `process`
ADD COLUMN `process_type` VARCHAR(31) NOT NULL AFTER `status`,
ADD COLUMN `process_role` BIGINT(20) AFTER `process_type`,
DROP COLUMN `observations`,
DROP COLUMN `decision_reason`,
ADD KEY `FK_emmws68ll8g6hlod8hvwk1t95` (`process_role`),
ADD CONSTRAINT `FK_emmws68ll8g6hlod8hvwk1t95` FOREIGN KEY (`process_role`) REFERENCES `process_role` (`id`);

UPDATE `process` SET `event`='';
UPDATE `process` SET `process_type`='Assessment';
UPDATE `process` SET `event`='recommend', `process_role`='7' WHERE `id`='1';
UPDATE `process` SET `event`='recommend', `process_role`='16' WHERE `id`='3';
UPDATE `process` SET `event`='recommend', `process_role`='22' WHERE `id`='7';
UPDATE `process` SET `process_role`='8' WHERE `id`='2';
UPDATE `process` SET `process_role`='17' WHERE `id`='4';
UPDATE `process` SET `process_role`='20' WHERE `id`='5';
UPDATE `process` SET `process_role`='21' WHERE `id`='6';
UPDATE `process` SET `process_role`='23' WHERE `id`='8';

DROP TABLE IF EXISTS `assessment`;

