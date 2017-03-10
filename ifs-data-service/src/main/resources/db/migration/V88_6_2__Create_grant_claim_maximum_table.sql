CREATE TABLE `grant_claim_maximum` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT(20) NOT NULL,
  `organisation_size_id` BIGINT(20) NULL,
  `organisation_type_id` BIGINT(20) NOT NULL,
  `competition_type_id` BIGINT(20) NOT NULL,
  `maximum` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `unique_grant_claim_maximum` UNIQUE (`category_id`,`organisation_size_id`,`organisation_type_id`, `competition_type_id`),
  CONSTRAINT `grant_claim_maximum_category_fk` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `grant_claim_maximum_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`),
  CONSTRAINT `grant_claim_maximum_organisation_type_fk` FOREIGN KEY (`organisation_type_id`) REFERENCES `organisation_type` (`id`),
  CONSTRAINT `grant_claim_maximum_competition_type_fk` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;