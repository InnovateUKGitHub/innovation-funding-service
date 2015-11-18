CREATE TABLE `form_validator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clazz_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;


CREATE TABLE `form_type_form_validator` (
  `form_input_type_id` bigint(20) NOT NULL,
  `form_validator_id` bigint(20) NOT NULL,
  KEY `FK_1d9wf2qse99wee3943s3n5g1c` (`form_validator_id`),
  KEY `FK_2cs0suxygrahxifnx562w9kmk` (`form_input_type_id`),
  CONSTRAINT `FK_1d9wf2qse99wee3943s3n5g1c` FOREIGN KEY (`form_validator_id`) REFERENCES `form_validator` (`id`),
  CONSTRAINT `FK_2cs0suxygrahxifnx562w9kmk` FOREIGN KEY (`form_input_type_id`) REFERENCES `form_input_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
