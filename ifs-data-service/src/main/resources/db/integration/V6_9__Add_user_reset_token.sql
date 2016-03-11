INSERT  IGNORE INTO `token` (`id`, `class_name`, `class_pk`, `extra_info`, `hash`, `type`)
    VALUES (10,'com.worth.ifs.user.domain.User',1,'{}','a2e2928b-960f-469d-859f-f038b2bd9f42','RESET_PASSWORD');





-- Add new invite + invite organisation, accept invite, create user, create organisation, send email verification token.
 -- This is all done on application 1
 INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (8,'HIVE IT LIMITED','08852342',NULL,1);
 INSERT  IGNORE INTO `invite_organisation` (`id`, `organisation_name`, `organisation_id`) VALUES (3,'Hive IT',8);
 INSERT  IGNORE INTO `invite` (`id`, `email`, `hash`, `name`, `status`, `application_id`, `invite_organisation_id`) VALUES (4,'ewan+1@hiveit.co.uk','4b0bda604555e5e42944924437139abbce83bdea979c957288c4aadda8a403bf3a3f683f478fb5b7','Ewan 1 ','ACCEPTED',1,3);
 INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `token`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`) VALUES (10,'ewan+1@hiveit.co.uk',NULL,'Ewan Cormack','11ac77d42a702fc148f0fa51c11f0dd02d8389bd17ab7434ff9ea5a9c4a03e1b7608fd4fe8091d4c','10abc123','Ewan',NULL,'Cormack','34567890','Mr','INACTIVE');
 INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (10,8);
 INSERT  IGNORE INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (1,'Electric Works, Sheffield Digital Campus','Concourse Way','','Sheffield','S1 2BJ','');
 INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (10,4);
 INSERT  IGNORE INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (1,'REGISTERED',1,8);
 INSERT  IGNORE INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (28,1,8,2,10);
 INSERT  IGNORE INTO `token` (`id`, `class_name`, `class_pk`, `extra_info`, `hash`, `type`) VALUES (1,'com.worth.ifs.user.domain.User',10,'{}','8223991f065abb7ed909c8c7c772fbdd24c966d246abd63c2ff7eeba9add3bafe42b067b602f761b','VERIFY_EMAIL_ADDRESS');

 -- New application , unverified user.  ewan+2@hiveit.co.uk
 INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (9,'HIVE LIMITED','03937810',NULL,1);
 INSERT  IGNORE INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (2,'46 Butterton Road','Rhyl','','Denbighshire','LL18 1RF','');
 INSERT  IGNORE INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (2,'REGISTERED',2,9);
 INSERT  IGNORE INTO `token` (`id`, `class_name`, `class_pk`, `extra_info`, `hash`, `type`) VALUES (2,'com.worth.ifs.user.domain.User',11,'{\"competitionId\":1}','5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0','VERIFY_EMAIL_ADDRESS');
 INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `token`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`) VALUES (11,'ewan+2@hiveit.co.uk',NULL,'Ewan Cormack','fd9501780ec772f28d7fe0288601c874d2ae2a5d617ddfd79a1ce5c9ef46a75d579efcb55f62bb0a','11abc123','Ewan',NULL,'Cormack','345678','Mr','INACTIVE');
 INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (11,9);
 INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (11,4);

 -- New application , unverified user.  ewan+12@hiveit.co.uk
 INSERT  IGNORE INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (10,'HIVE 1 LTD','09771561',NULL,1);
 INSERT  IGNORE INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (3,'Ground Floor','Elizabeth House, 54/58 High Street','','Edgware','HA8 7EJ','Middlesex');
 INSERT  IGNORE INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (3,'REGISTERED',3,10);
 INSERT  IGNORE INTO `token` (`id`, `class_name`, `class_pk`, `extra_info`, `hash`, `type`) VALUES (3,'com.worth.ifs.user.domain.User',12,'{\"competitionId\":1}','4a5bc71c9f3a2bd50fada434d888579aec0bd53fe7b3ca3fc650a739d1ad5b1a110614708d1fa083','VERIFY_EMAIL_ADDRESS');
 INSERT  IGNORE INTO `user` (`id`, `email`, `image_url`, `name`, `password`, `token`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`) VALUES (12,'ewan+12@hiveit.co.uk',NULL,'Ewan Cormack','3760e73828b6cc58bcd2e0852ebffca78ceb600d1119ab35f8a17ee60d9edc6ce159e6ee773feba7','12abc123','Ewan',NULL,'Cormack','567890','Mr','INACTIVE');
 INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (12,10);
 INSERT  IGNORE INTO `user_role` (`user_id`, `role_id`) VALUES (12,4);