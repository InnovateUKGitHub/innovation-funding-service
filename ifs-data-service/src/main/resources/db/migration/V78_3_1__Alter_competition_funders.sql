ALTER TABLE `competition`
DROP COLUMN `funder_budget`,
DROP COLUMN `funder`;

ALTER TABLE `competition_co_funder`
DROP FOREIGN KEY `competition_co_funders_compitions_id`;

ALTER TABLE `competition_co_funder`
DROP INDEX `competition_co_funders_compitions_id_idx`;

ALTER TABLE `competition_co_funder`
CHANGE COLUMN `co_funder` `funder` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `co_funder_budget` `funder_budget` DECIMAL(10,2) NULL DEFAULT '0.00' ,
ADD COLUMN `co_funder` TINYINT(1) NOT NULL DEFAULT 0 AFTER `competition_id`, RENAME TO  `competition_funder` ;


ALTER TABLE `competition_funder`
ADD INDEX `competition_funders_competitions_id_idx` (`competition_id` ASC),
ADD CONSTRAINT `competition_funders_competitions_id_idx`
  FOREIGN KEY (`competition_id`)
  REFERENCES `competition` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;