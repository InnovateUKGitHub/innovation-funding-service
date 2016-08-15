CREATE TABLE `cost_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `cost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` decimal(14,2) DEFAULT NULL,
  `cost_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ae4qyairso4xgum92xhti20xm` (`cost_group_id`),
  CONSTRAINT `FK_ae4qyairso4xgum92xhti20xm` FOREIGN KEY (`cost_group_id`) REFERENCES `cost_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;