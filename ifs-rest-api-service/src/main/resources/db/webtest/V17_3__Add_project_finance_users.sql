/*
  This script adds some example users with project finance role
*/

INSERT  IGNORE INTO `project_finance_emails` (`email`) VALUES ('worth.email.test+project.finance1@gmail.com');
INSERT  IGNORE INTO `project_finance_emails` (`email`) VALUES ('worth.email.test+project.finance2@gmail.com');
INSERT  IGNORE INTO `project_finance_emails` (`email`) VALUES ('worth.email.test+project.finance3@gmail.com');
INSERT  IGNORE INTO `project_finance_emails` (`email`) VALUES ('worth.email.test+project.finance4@gmail.com');

INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (33,'project.finance1@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','d10035a2-0218-4b25-82c7-6d282910c6c3');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (34,'project.finance2@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','3aa41c96-4d79-49ab-aebe-e2096c4a640c');

INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (33,8);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (34,8);

INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (33,11);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (34,11);