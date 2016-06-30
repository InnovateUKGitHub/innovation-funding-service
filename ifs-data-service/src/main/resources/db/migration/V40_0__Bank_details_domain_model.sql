CREATE TABLE `bank_details` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sort_code` VARCHAR(6) NOT NULL,
  `account_number` VARCHAR(8) NOT NULL,
  `project_id` BIGINT(20) NOT NULL,
  `organisation_address_id` BIGINT(20) NOT NULL,
  `organisation_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`));