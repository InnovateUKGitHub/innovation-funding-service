ALTER TABLE `user`
    ADD COLUMN `edi_status` ENUM('COMPLETE', 'INCOMPLETE', 'INPROGRESS') NULL AFTER `modified_on`,
    ADD COLUMN `edi_review_date` DATETIME NULL AFTER `edi_status`;

