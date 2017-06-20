CREATE TABLE `assessor_count_option` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_type_id` bigint(20) NOT NULL COMMENT 'Competition Type Id.',
  `option_name` varchar(100) NOT NULL COMMENT 'Assessor option name which can be used as a label in the front end. This give us flexibility to use different values for label and submission.',
  `option_value` int(4) NOT NULL COMMENT 'Assessor option value to be used for any business logic.',
  `default_option` tinyint(1) DEFAULT '0' COMMENT 'Is this option to be shown as selected by default.',
  PRIMARY KEY (`id`),
  KEY `competition_type_id_assessor_count_idx` (`competition_type_id`),
  CONSTRAINT `FK_assessor_count_option_competition_type` FOREIGN KEY (`competition_type_id`) REFERENCES `competition_type` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COMMENT='Table to store the options for the assessor that are used for various competition types.';


ALTER TABLE `competition`
ADD COLUMN `assessor_count` INT(4) NULL DEFAULT 0 AFTER `include_growth_table`,
ADD COLUMN `assessor_pay` DECIMAL(10,2) NULL DEFAULT '0.00' AFTER `assessor_count`;
