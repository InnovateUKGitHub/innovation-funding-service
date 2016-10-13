/*
  This script adds some example user with ifs admin role and Innovate UK as organisation
*/

INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`) VALUES (54,'ifsadmin@innovateuk.test','image3.jpg',NULL,NULL,NULL,NULL,NULL,'ACTIVE','071bc46a-e141-48d1-96db-81def61f8850');
INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (54,14);
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (54,11);