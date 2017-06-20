ALTER TABLE `project`
    ADD COLUMN `grant_offer_letter_file_entry_id` BIGINT(20) NULL,
    ADD COLUMN `additional_contract_file_entry_id` BIGINT(20) NULL,
    ADD COLUMN `signed_grant_offer_file_entry_id` BIGINT(20) NULL,
    ADD COLUMN `offer_rejected` bit(1) NOT NULL,
    ADD COLUMN `offer_submitted_date` DATETIME NULL
    ;