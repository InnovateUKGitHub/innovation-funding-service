DROP TABLE `form_type_form_validator`;

CREATE TABLE `form_input_validator` (
  `form_input_id` bigint(20) NOT NULL,
  `form_validator_id` bigint(20) NOT NULL,
  PRIMARY KEY (`form_input_id`,`form_validator_id`),
  KEY `FK_y95iwkay85fdurj700i6i188` (`form_validator_id`),
  CONSTRAINT `FK_lfojcj07kbeklgn9v0g0hg3sb` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_y95iwkay85fdurj700i6i188` FOREIGN KEY (`form_validator_id`) REFERENCES `form_validator` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('1', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('2', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('3', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('4', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('5', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('6', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('7', '2');
INSERT INTO `form_input_validator` (`form_input_id`, `form_validator_id`) VALUES ('8', '2');
