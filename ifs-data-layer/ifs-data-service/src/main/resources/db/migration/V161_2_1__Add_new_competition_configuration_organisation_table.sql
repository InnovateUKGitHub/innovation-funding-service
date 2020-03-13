CREATE TABLE competition_organisation_config (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(20) NOT NULL,
  `international_organisations_allowed` BIT(1) DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `competition_id_UNIQUE` (`competition_id`),
  CONSTRAINT `competition_organisation_config_fk` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
