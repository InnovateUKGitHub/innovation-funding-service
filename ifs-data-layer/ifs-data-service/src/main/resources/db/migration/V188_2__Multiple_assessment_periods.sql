-- IFS-8846, multiple assessment periods changes

ALTER TABLE milestone
        ADD COLUMN parent_id BIGINT(20) DEFAULT NULL,
        ADD CONSTRAINT `FK_milestone_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `milestone` (`id`);

ALTER TABLE process
        ADD COLUMN milestone_id BIGINT(20) DEFAULT NULL,
        ADD CONSTRAINT `FK_process_milestone_id` FOREIGN KEY (`milestone_id`) REFERENCES `milestone` (`id`);

