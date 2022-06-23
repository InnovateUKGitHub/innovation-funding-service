CREATE TABLE competition_horizon_work_programme (
id bigint(20) NOT NULL AUTO_INCREMENT,
competition_id bigint(20) NOT NULL,
work_programme_id bigint(20) NOT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `k_competition_horizon_work_programme` UNIQUE (`competition_id`, work_programme_id),
CONSTRAINT `fk_competition` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
CONSTRAINT `fk_horizon_work_programme` FOREIGN KEY (`work_programme_id`) REFERENCES `horizon_work_programme` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

