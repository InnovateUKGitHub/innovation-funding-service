CREATE TABLE IF NOT EXISTS `setup_status` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `completed` BIT(1) NOT NULL,
  `class_name` VARCHAR(255) NOT NULL,
  `class_pk` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20) NULL,
  `target_id` BIGINT(20) NULL,
  `target_class_name` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_setup_status_idx` (`parent_id` ASC),
  CONSTRAINT `FK_setup_status_id`
    FOREIGN KEY (`parent_id`)
    REFERENCES `setup_status` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB