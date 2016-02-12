
-- Add Cascades , so that if we remove the application_finance row, the cost and cost values are also removed.
ALTER TABLE `cost`
DROP FOREIGN KEY `FK_14n47e1gx72ud7hj3t2yscu1v`;
ALTER TABLE `cost`
ADD CONSTRAINT `FK_14n47e1gx72ud7hj3t2yscu1v`
  FOREIGN KEY (`application_finance_id`)
  REFERENCES `application_finance` (`id`)
  ON DELETE CASCADE;


ALTER TABLE `cost_value`
DROP FOREIGN KEY `FK_cryaaiuibh4b0sqw3aqrkspmb`;
ALTER TABLE `cost_value`
ADD CONSTRAINT `FK_cryaaiuibh4b0sqw3aqrkspmb`
  FOREIGN KEY (`cost_id`)
  REFERENCES `cost` (`id`)
  ON DELETE CASCADE;

-- Use a tmp table to remove the duplicate keys
CREATE TABLE `application_finance_tmp` LIKE `application_finance`;

ALTER TABLE `application_finance_tmp` ADD UNIQUE INDEX `UK_application_id_organisation_id` (`application_id` ASC, `organisation_id` ASC)  COMMENT '';

INSERT IGNORE INTO `application_finance_tmp` SELECT * FROM `application_finance`;
SET SQL_SAFE_UPDATES = 0;
DELETE FROM `application_finance`
WHERE
    `id` NOT IN (SELECT 
        `id`
    FROM
        application_finance_tmp);
SET SQL_SAFE_UPDATES = 1;

DROP TABLE `application_finance_tmp`;


-- And finally, add the unique index.
ALTER TABLE `application_finance` ADD UNIQUE INDEX `UK_application_id_organisation_id` (`application_id` ASC, `organisation_id` ASC)  COMMENT '';
