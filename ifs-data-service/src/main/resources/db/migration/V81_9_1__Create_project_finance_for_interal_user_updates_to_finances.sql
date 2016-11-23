CREATE TABLE `project_finance` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT(20) NOT NULL,
  `organisation_id` BIGINT(20) NOT NULL,
  `organisation_size` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_project_finance_to_project_idx` (`project_id` ASC),
  INDEX `FK_project_finance_to_organisation_idx` (`organisation_id` ASC),
  CONSTRAINT `FK_project_finance_to_project`
  FOREIGN KEY (`project_id`)
  REFERENCES `project` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `FK_project_finance_to_organisation`
  FOREIGN KEY (`organisation_id`)
  REFERENCES `organisation` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);
