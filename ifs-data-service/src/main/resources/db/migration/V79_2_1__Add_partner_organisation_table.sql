CREATE TABLE `partner_organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organisation_id` bigint(20) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `lead_organisation` BIT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b3476se7may81i65fpmjt2jte` (`project_id`,`organisation_id`),
  KEY `FK_fpatn7eo4gdhqi5tej589n7wk` (`organisation_id`),
  CONSTRAINT `FK_fpatn7eo4gdhqi5tej589n7wk` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`),
  CONSTRAINT `FK_h4cpvkntxf8g7mp4i6fe6eoon` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;