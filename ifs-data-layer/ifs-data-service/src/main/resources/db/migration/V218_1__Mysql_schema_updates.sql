-- IFS-9443 - Apply updates to databases where db is not rebuilt - alter statements can be run safely a second time

-- V120_3_1__Alter_table_terms_and_conditions.sql
ALTER TABLE `terms_and_conditions` CHANGE COLUMN `created_on` `created_on` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `terms_and_conditions` CHANGE COLUMN `modified_on` `modified_on` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- V128_3_1__Add_auditable_fields_to_competition.sql
ALTER TABLE `competition` CHANGE COLUMN `created_on` `created_on` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `competition` CHANGE COLUMN `modified_on` `modified_on` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- V167_1_1__Add_multiple_file_entry_table.sql
-- Fixed already