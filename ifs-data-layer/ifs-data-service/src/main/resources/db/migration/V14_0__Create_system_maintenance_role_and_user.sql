INSERT IGNORE INTO `role` (`name`, `url`) VALUES ('system_maintainer','');

INSERT INTO `user` (`email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`, `system_user`) VALUES ('ifs_system_maintenance_user@innovateuk.org',NULL,'IFS',NULL,'System Maintenance User',NULL,NULL,'ACTIVE','88c9c1ba-645f-4a85-95dc-c0bb165caac2',1);

INSERT INTO `user_role` (`user_id`, `role_id`)
  SELECT u.id, r.id FROM `user` u, `role` r where u.email = 'ifs_system_maintenance_user@innovateuk.org' AND r.name = 'system_maintainer';