
/** finance_row will now be linked with either application or project depending on row_type **/
ALTER TABLE `finance_row` DROP FOREIGN KEY `FK_14n47e1gx72ud7hj3t2yscu1v`;

/**Add new columns needed to support adding new rows for project finance **/
ALTER TABLE `finance_row`
  CHANGE COLUMN `application_finance_id` `target_id` BIGINT(20) NOT NULL,
  ADD COLUMN `application_row_id` BIGINT(20) NULL DEFAULT NULL ,
  ADD COLUMN `row_type` VARCHAR(255) NOT NULL;