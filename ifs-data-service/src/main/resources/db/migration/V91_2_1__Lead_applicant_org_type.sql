CREATE TABLE `lead_applicant_type` (
  `competition_id` BIGINT NOT NULL,
  `organisation_type_id` BIGINT NOT NULL,
  PRIMARY KEY (`organisation_type_id`, `competition_id`),
  INDEX `competition_id_fk_idx` (`competition_id` ASC),
  CONSTRAINT `competition_id_fk`
    FOREIGN KEY (`competition_id`)
    REFERENCES `competition` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `org_type_id_fk`
    FOREIGN KEY (`organisation_type_id`)
    REFERENCES `organisation_type` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE);

ALTER TABLE `organisation_type`
ADD COLUMN `visible_in_setup` BIT(1) NULL DEFAULT NULL AFTER `parent_organisation_type_id`;

