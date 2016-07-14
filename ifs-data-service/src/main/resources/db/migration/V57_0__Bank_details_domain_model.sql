CREATE TABLE `bank_details` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sort_code` VARCHAR(6) NOT NULL,
  `account_number` VARCHAR(8) NOT NULL,
  `project_id` BIGINT(20) NOT NULL,
  `organisation_address_id` BIGINT(20) NOT NULL,
  `organisation_id` BIGINT(20) NOT NULL,
  `company_name_score` TINYINT(1) NULL,
  `company_number_score` TINYINT(1) NULL,
  `account_number_score` TINYINT(1) NULL,
  `address_score` TINYINT(1) NULL,
  `manual_approval` TINYINT(1) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `bank_details_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `bank_details_to_organisation_address_fk` FOREIGN KEY (`organisation_address_id`) REFERENCES `organisation_address` (`id`),
  CONSTRAINT `bank_details_to_organisation_fk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
);

INSERT INTO `address_type`(id, name) VALUES (4, 'BANK_DETAILS');