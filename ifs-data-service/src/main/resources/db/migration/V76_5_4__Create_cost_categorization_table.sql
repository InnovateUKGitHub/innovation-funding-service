CREATE TABLE `cost_categorization` (
  `cost_id` bigint(20) NOT NULL,
  `cost_category_id` bigint(20) NOT NULL,
  PRIMARY KEY (`cost_category_id`),
  KEY `FK_eaqwxt1nx926138w5wkmfs1gt` (`cost_id`),
  CONSTRAINT `FK_8mmq5unyree8h9f3l30fmw579` FOREIGN KEY (`cost_category_id`) REFERENCES `cost_category` (`id`),
  CONSTRAINT `FK_eaqwxt1nx926138w5wkmfs1gt` FOREIGN KEY (`cost_id`) REFERENCES `cost` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;