CREATE TABLE finance_check (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  project_id BIGINT(20) NOT NULL,
  cost_group_id BIGINT(20) NOT NULL,
  PRIMARY KEY (id),
  KEY `FK_finance_check_project` (`project_id`),
  KEY `FK_finance_check_cost_group` (`cost_group_id`),
  CONSTRAINT `FK_finance_check_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_finance_check_cost_group` FOREIGN KEY (`cost_group_id`) REFERENCES `cost_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;