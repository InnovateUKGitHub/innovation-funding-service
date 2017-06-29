CREATE TABLE `cost_time_period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration_amount` int(11) NOT NULL,
  `duration_unit` varchar(255) NOT NULL,
  `offset_amount` int(11) NOT NULL,
  `offset_unit` varchar(255) NOT NULL,
  `cost_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7a1kluy79whovi3c0ik0wmrf0` (`cost_id`),
  CONSTRAINT `FK_7a1kluy79whovi3c0ik0wmrf0` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



