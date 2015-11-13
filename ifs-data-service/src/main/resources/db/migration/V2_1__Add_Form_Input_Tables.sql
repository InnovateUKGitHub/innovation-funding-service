CREATE TABLE `form_input_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



CREATE TABLE `form_input` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `option_values` longtext,
  `word_count` int(11) DEFAULT NULL,
  `form_input_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pvbo288244dfas1gd12t17pkv` (`form_input_type_id`),
  CONSTRAINT `FK_pvbo288244dfas1gd12t17pkv` FOREIGN KEY (`form_input_type_id`) REFERENCES `form_input_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



CREATE TABLE `form_input_response` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `update_date` datetime DEFAULT NULL,
  `value` longtext,
  `form_input_id` bigint(20) DEFAULT NULL,
  `updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7901a8ft9tx1e02r82t84feaj` (`form_input_id`),
  KEY `FK_e83s9n8p6d60v1on2730jf8m9` (`updated_by_id`),
  CONSTRAINT `FK_e83s9n8p6d60v1on2730jf8m9` FOREIGN KEY (`updated_by_id`) REFERENCES `process_role` (`id`),
  CONSTRAINT `FK_7901a8ft9tx1e02r82t84feaj` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



CREATE TABLE `question_form_input` (
  `question_id` bigint(20) NOT NULL,
  `form_input_id` bigint(20) NOT NULL,
  `priority` int(11) NOT NULL,
  UNIQUE KEY `UK_8wu8lh9w3o8jwtvgrkgwn82ad` (`form_input_id`),
  KEY `FK_qwtjaey2uqlx08ccjpb02k7vl` (`question_id`),
  CONSTRAINT `FK_8wu8lh9w3o8jwtvgrkgwn82ad` FOREIGN KEY (`form_input_id`) REFERENCES `form_input` (`id`),
  CONSTRAINT `FK_qwtjaey2uqlx08ccjpb02k7vl` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;