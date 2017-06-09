-- DO NOT MERGE TO DEV THIS IS TEMPORARY PATCH
INSERT INTO `user` (`email`, `first_name`, `invite_name`, `last_name`, `status`, `uid`, `gender`, `allow_marketing_emails`) VALUES ('admin@innovateuk.test', 'IFS', NULL, 'Administrator', 'ACTIVE', 'e6999555-9298-48d7-8475-afce4b83710b', 'FEMALE', '0');
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('254', '14');
UPDATE `role` SET `url`='management/dashboard' WHERE `id`='14';