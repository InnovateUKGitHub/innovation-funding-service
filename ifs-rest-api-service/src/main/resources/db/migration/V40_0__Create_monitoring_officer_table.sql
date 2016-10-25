--
-- Table structure for table `monitoring_officer`
--
CREATE TABLE `monitoring_officer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL, /* Datatype is varchar since phone can contain + and - */
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fk_project_id_UNIQUE` (`project_id`),
  KEY `monitoring_officer_to_project_fk` (`project_id`),
  CONSTRAINT `monitoring_officer_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;