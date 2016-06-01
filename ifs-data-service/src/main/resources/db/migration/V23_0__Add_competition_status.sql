ALTER TABLE `competition`
    ADD COLUMN `status` VARCHAR(45) NULL DEFAULT NULL COMMENT '' AFTER `funders_panel_end_date`;
