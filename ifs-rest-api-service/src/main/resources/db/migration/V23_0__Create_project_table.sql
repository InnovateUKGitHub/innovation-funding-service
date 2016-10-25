DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` BIGINT(20) NOT NULL,
  `duration_in_months` BIGINT(20) NULL,
  `address` BIGINT(20) NULL,
  `target_start_date` DATE NULL,
  `project_manager` BIGINT(20) NULL,
  PRIMARY KEY (`id`));