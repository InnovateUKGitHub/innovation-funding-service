ALTER TABLE `application_pre_registration_config` ADD COLUMN `pre_registration_application_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `application_pre_registration_config` ADD CONSTRAINT `fk_pre_registration_application_id` FOREIGN KEY(`pre_registration_application_id`) REFERENCES `application` (`id`);
