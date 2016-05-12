
CREATE TABLE `funding_decision_data` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=964 DEFAULT CHARSET=utf8;

CREATE TABLE `funding_decision_data_application_decision` (
  `funding_decision_id` bigint(20) NOT NULL,
  `application_id` varchar(255) NOT NULL,
  `funding_decision` varchar(255) NOT NULL,
  PRIMARY KEY (`funding_decision_id`, `application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=964 DEFAULT CHARSET=utf8;
