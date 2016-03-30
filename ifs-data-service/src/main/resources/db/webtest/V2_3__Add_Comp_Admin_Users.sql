/*
  This script adds some example users with comp admin role and Innovate UK as organisation
*/

INSERT  IGNORE INTO `comp_admin_emails` (`email`) VALUES ('worth.email.test.admin1@gmail.com');
INSERT  IGNORE INTO `comp_admin_emails` (`email`) VALUES ('worth.email.test.admin2@gmail.com');
INSERT  IGNORE INTO `comp_admin_emails` (`email`) VALUES ('worth.email.test.admin3@gmail.com');
INSERT  IGNORE INTO `comp_admin_emails` (`email`) VALUES ('worth.email.test.admin4@gmail.com');

INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (15,'john.doe@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','571bc46a-e141-48d1-96db-81def61f8859');
INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (16,'robert.johnson@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','0aad8dad-9a45-4b06-ba4d-7a9520685e69');

INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (15,5);
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (16,5);

INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (11,'Innovate UK',NULL,NULL,1);

INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (15,11);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (16,11);
