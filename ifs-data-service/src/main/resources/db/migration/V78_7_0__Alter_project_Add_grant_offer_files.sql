ALTER TABLE `project`
    ADD COLUMN `grant_offer_letter_file_entry_id` BIGINT(20) NULL,
    ADD COLUMN `additional_contract_file_entry_id` BIGINT(20) NULL,
    ADD COLUMN `offer_signed` BIT(1) DEFAULT 0,
    ADD COLUMN `offer_rejected` BIT(1) DEFAULT 0,
    ADD COLUMN `offer_submitted_date` DATETIME NULL
    ;