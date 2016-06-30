ALTER TABLE `bank_details`
  ADD COLUMN `company_name_score` TINYINT(1) NULL AFTER `organisation_address_id`,
  ADD COLUMN `company_number_score` TINYINT(1) NULL AFTER `company_name_score`,
  ADD COLUMN `account_number_score` TINYINT(1) NULL AFTER `company_number_score`,
  ADD COLUMN `address_score` TINYINT(1) NULL AFTER `account_number_score`,
  ADD COLUMN `manual_approval` TINYINT(1) NULL AFTER `address_score`;