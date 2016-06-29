CREATE TABLE `bank_detail` (
  `id` BIGINT(20) NOT NULL,
  `sort_code` VARCHAR(6) NOT NULL,
  `account_number` VARCHAR(8) NOT NULL,
  `project_id` BIGINT(20) NOT NULL,
  `organisation_address_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`));