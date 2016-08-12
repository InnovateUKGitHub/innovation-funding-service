CREATE TABLE `cost_category_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `cost_category_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `cost_category_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gacypps10lfd1wqy6v31r5eoo` (`cost_category_group_id`),
  CONSTRAINT `FK_gacypps10lfd1wqy6v31r5eoo` FOREIGN KEY (`cost_category_group_id`) REFERENCES `cost_category_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `cost_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `cost_category_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_py0mpvk8bt1klihl44whkqmp5` (`cost_category_group_id`),
  CONSTRAINT `FK_py0mpvk8bt1klihl44whkqmp5` FOREIGN KEY (`cost_category_group_id`) REFERENCES `cost_category_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;