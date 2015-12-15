
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_line1` varchar(255) DEFAULT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `care_of` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `locality` varchar(255) DEFAULT NULL,
  `po_box` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `organisation_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_type` varchar(255) DEFAULT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g3y4ooi9akaq8e98efgmljigm` (`organisation_id`,`address_id`),
  KEY `FK_prle5vffxxpi4ibqymh6ic87x` (`address_id`),
  CONSTRAINT `FK_k8ipyjlxpsqfga85v2vhg0m0x` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_prle5vffxxpi4ibqymh6ic87x` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



ALTER TABLE `organisation` ADD COLUMN `company_house_number` VARCHAR(255) NULL DEFAULT NULL COMMENT '';
ALTER TABLE `organisation` ADD COLUMN `organisation_size` VARCHAR(255) NULL DEFAULT NULL COMMENT '';

