CREATE TABLE `cost_total` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `finance_id` bigint(20) NOT NULL,
  `type` varchar(255) COLLATE utf8_bin NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `total` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`finance_id`,`type`,`name`)
);