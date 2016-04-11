/*
  This script adds some example users with comp admin role and Innovate UK as organisation
*/

INSERT  IGNORE INTO `comp_admin_emails` (`email`) VALUES ('compadmin@innovateuk.test');

INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (18,'compadmin@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','571bc46a-e141-48d1-96db-81def61f8859');

INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (18,5);

INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (8,'Innovate UK',NULL,NULL,1);

INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (18,8);
