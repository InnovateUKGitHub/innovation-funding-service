CREATE TABLE `guidance_row` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `form_input_id` bigint(20) NOT NULL,
  `subject` varchar(100) NOT NULL,
  `justification` varchar(255) NOT NULL,
  `priority` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `fk_condition_form_input_idx` (`form_input_id`),
  CONSTRAINT `fk_condition_form_input` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
