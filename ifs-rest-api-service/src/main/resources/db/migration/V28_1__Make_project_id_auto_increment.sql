ALTER TABLE `project_user` DROP FOREIGN KEY `project_user_to_project_fk`;
ALTER TABLE project MODIFY `id` BIGINT(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE project_user ADD CONSTRAINT `project_user_to_project_fk` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`);
