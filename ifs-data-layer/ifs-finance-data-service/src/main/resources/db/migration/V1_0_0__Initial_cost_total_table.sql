CREATE TABLE `cost_total` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `finance_id` BIGINT(20) NOT NULL,
  `type` VARCHAR(255) COLLATE utf8_bin NOT NULL,
  `name` VARCHAR(255) COLLATE utf8_bin NOT NULL,
  `total` DECIMAL(14, 2) DEFAULT 0.00,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`finance_id`,`type`,`name`)
);