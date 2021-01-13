-- IFS-8846, multiple assessment periods changes

CREATE TABLE `assessment_period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `competition_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_assessment_period_competition_id FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
);


ALTER TABLE milestone
        DROP INDEX milestone_unique_competition_type;

ALTER TABLE milestone
        ADD COLUMN parent_id BIGINT(20) DEFAULT NULL,
        ADD CONSTRAINT `FK_milestone_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `assessment_period` (`id`);

ALTER TABLE application
        ADD COLUMN assessment_period_id BIGINT(20) DEFAULT NULL,
        ADD CONSTRAINT `FK_assessment_period_id` FOREIGN KEY (`assessment_period_id`) REFERENCES `assessment_period` (`id`);
