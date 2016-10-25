
INSERT INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (18,'1','Theremin Road',NULL,'Bath','BA1 5LR','Avon');
INSERT INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (19,'1','Theremin Avenue',NULL,'Bristol','BA1 5LR','Avon');
INSERT INTO `address` (`id`, `address_line1`, `address_line2`, `address_line3`, `town`, `postcode`, `county`) VALUES (20,'1','Theremin Street',NULL,'Cardiff','BA1 5LR','Avon');


INSERT INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`, `funding_decision`, `assessor_feedback_file_entry_id`) VALUES (21,3,'Playing the theremin behind your head','2020-10-01',3,5,'2016-05-24 10:17:55','FUNDED',NULL);
INSERT INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`, `funding_decision`, `assessor_feedback_file_entry_id`) VALUES (22,3,'Smash your theremin into pieces onstage','2020-10-01',3,5,'2016-05-24 10:20:54','FUNDED',NULL);
INSERT INTO `application` (`id`, `duration_in_months`, `name`, `start_date`, `application_status_id`, `competition`, `submitted_date`, `funding_decision`, `assessor_feedback_file_entry_id`) VALUES (23,3,'Theremins are weird and nobody knows what they are','2020-10-01',4,5,'2016-05-24 10:23:43','UNFUNDED',NULL);


INSERT INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (26,'Electric Sounds Ltd','06477798',NULL,1);
INSERT INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (27,'Radiowaves Ltd','06477798',NULL,1);
INSERT INTO `organisation` (`id`, `name`, `company_house_number`, `organisation_size`, `organisation_type_id`) VALUES (28,'Theremins Rock Ltd','06477798',NULL,1);


INSERT INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`, `finance_file_entry_id`) VALUES (25,21,26,'SMALL',NULL);
INSERT INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`, `finance_file_entry_id`) VALUES (26,22,27,'SMALL',NULL);
INSERT INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`, `finance_file_entry_id`) VALUES (27,23,28,'SMALL',NULL);


INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (228,NULL,'Working days per year','Working days per year',232,'working-days-per-year',25,28);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (229,NULL,'','NONE',0,'overhead',25,29);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (230,0,'Grant Claim','',20,'grant-claim',25,38);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (231,0,'Other Funding','No',0,'other-funding',25,35);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (232,18000,'','builder',50,'',25,130);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (233,NULL,'Working days per year','Working days per year',232,'working-days-per-year',26,28);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (234,NULL,'','NONE',0,'overhead',26,29);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (235,0,'Grant Claim','',20,'grant-claim',26,38);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (236,0,'Other Funding','No',0,'other-funding',26,35);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (237,24000,'','builder',90,'',26,130);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (238,NULL,'Working days per year','Working days per year',232,'working-days-per-year',27,28);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (239,NULL,'','NONE',0,'overhead',27,29);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (240,0,'Grant Claim','',30,'grant-claim',27,38);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (241,0,'Other Funding','No',0,'other-funding',27,35);
INSERT INTO `cost` (`id`, `cost`, `description`, `item`, `quantity`, `name`, `application_finance_id`, `question_id`) VALUES (242,60000,'','builder',100,'',27,130);


INSERT INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`, `system_user`) VALUES (35,'test15@test.test',NULL,'test',NULL,'fifteen','1234567890','Mr','ACTIVE','6295834a-2126-4a2c-ba50-ce43be8bb1a8',0);
INSERT INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`, `system_user`) VALUES (36,'test16@test.test',NULL,'test',NULL,'sixteen','1234567890','Mr','ACTIVE','0be78cc3-4d52-4322-b89a-9e97e9ca1640',0);
INSERT INTO `user` (`id`, `email`, `image_url`, `first_name`, `invite_name`, `last_name`, `phone_number`, `title`, `status`, `uid`, `system_user`) VALUES (37,'test17@test.test',NULL,'test',NULL,'seventeen','1234567890','Mr','ACTIVE','bd4d12a6-8677-4685-b459-1a0252d0e525',0);


INSERT INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (44,21,26,1,35);
INSERT INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (45,22,27,1,36);
INSERT INTO `process_role` (`id`, `application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (46,23,28,1,37);


INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (75,'2016-05-24 10:16:26','first',118,44,21,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (76,'2016-05-24 10:16:44','second',119,44,21,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (77,'2016-05-24 10:16:51','third',120,44,21,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (78,'2016-05-24 10:16:58','fourth',121,44,21,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (79,'2016-05-24 10:17:04','fifth',122,44,21,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (80,'2016-05-24 10:19:38','first',118,45,22,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (81,'2016-05-24 10:19:47','second',119,45,22,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (82,'2016-05-24 10:19:54','third',120,45,22,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (83,'2016-05-24 10:20:04','fourth',121,45,22,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (84,'2016-05-24 10:20:10','fifth',122,45,22,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (85,'2016-05-24 10:22:40','first',118,46,23,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (86,'2016-05-24 10:22:47','second',119,46,23,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (87,'2016-05-24 10:22:52','third',120,46,23,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (88,'2016-05-24 10:22:58','fourth',121,46,23,NULL);
INSERT INTO `form_input_response` (`id`, `update_date`, `value`, `form_input_id`, `updated_by_id`, `application_id`, `file_entry_id`) VALUES (89,'2016-05-24 10:23:05','fifth',122,46,23,NULL);


INSERT INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (18,'REGISTERED',18,26);
INSERT INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (19,'REGISTERED',19,27);
INSERT INTO `organisation_address` (`id`, `address_type`, `address_id`, `organisation_id`) VALUES (20,'REGISTERED',20,28);


INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (484,NULL,'',NULL,21,NULL,NULL,44,118);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (485,NULL,'',NULL,21,NULL,NULL,44,119);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (486,NULL,'',NULL,21,NULL,NULL,44,120);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (487,NULL,'',NULL,21,NULL,NULL,44,121);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (488,NULL,'',NULL,21,NULL,NULL,44,122);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (489,NULL,'',NULL,21,NULL,NULL,44,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (490,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (491,NULL,'',NULL,21,NULL,NULL,44,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (492,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (493,NULL,'',NULL,21,NULL,NULL,44,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (494,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (495,NULL,'',NULL,21,NULL,NULL,44,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (496,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (497,NULL,'',NULL,21,NULL,NULL,44,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (498,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (499,NULL,'',NULL,21,NULL,NULL,44,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (500,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (501,NULL,'',NULL,21,NULL,NULL,44,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (502,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (503,NULL,'',NULL,21,NULL,NULL,44,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (504,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (505,NULL,'',NULL,21,NULL,NULL,44,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (506,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (507,NULL,'',NULL,21,NULL,NULL,44,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (508,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (509,NULL,'',NULL,21,NULL,NULL,44,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (510,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (511,NULL,'',NULL,21,NULL,NULL,44,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (512,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (513,NULL,'',NULL,21,NULL,NULL,44,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (514,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (515,NULL,'',NULL,21,NULL,NULL,44,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (516,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (517,NULL,'',NULL,21,NULL,NULL,44,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (518,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (519,NULL,'',NULL,21,NULL,NULL,44,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (520,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (521,NULL,'',NULL,21,NULL,NULL,44,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (522,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (523,NULL,'',NULL,21,NULL,NULL,44,127);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (524,'2016-05-24 10:17:39',NULL,'',21,44,44,NULL,127);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (525,NULL,'',NULL,22,NULL,NULL,45,118);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (526,NULL,'',NULL,22,NULL,NULL,45,119);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (527,NULL,'',NULL,22,NULL,NULL,45,120);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (528,NULL,'',NULL,22,NULL,NULL,45,121);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (529,NULL,'',NULL,22,NULL,NULL,45,122);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (530,NULL,'',NULL,22,NULL,NULL,45,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (531,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (532,NULL,'',NULL,22,NULL,NULL,45,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (533,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (534,NULL,'',NULL,22,NULL,NULL,45,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (535,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (536,NULL,'',NULL,22,NULL,NULL,45,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (537,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (538,NULL,'',NULL,22,NULL,NULL,45,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (539,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (540,NULL,'',NULL,22,NULL,NULL,45,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (541,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (542,NULL,'',NULL,22,NULL,NULL,45,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (543,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (544,NULL,'',NULL,22,NULL,NULL,45,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (545,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (546,NULL,'',NULL,22,NULL,NULL,45,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (547,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (548,NULL,'',NULL,22,NULL,NULL,45,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (549,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (550,NULL,'',NULL,22,NULL,NULL,45,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (551,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (552,NULL,'',NULL,22,NULL,NULL,45,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (553,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (554,NULL,'',NULL,22,NULL,NULL,45,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (555,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (556,NULL,'',NULL,22,NULL,NULL,45,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (557,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (558,NULL,'',NULL,22,NULL,NULL,45,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (559,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (560,NULL,'',NULL,22,NULL,NULL,45,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (561,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (562,NULL,'',NULL,22,NULL,NULL,45,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (563,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (564,NULL,'',NULL,22,NULL,NULL,45,127);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (565,'2016-05-24 10:20:42',NULL,'',22,45,45,NULL,127);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (566,NULL,'',NULL,23,NULL,NULL,46,118);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (567,NULL,'',NULL,23,NULL,NULL,46,119);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (568,NULL,'',NULL,23,NULL,NULL,46,120);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (569,NULL,'',NULL,23,NULL,NULL,46,121);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (570,NULL,'',NULL,23,NULL,NULL,46,122);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (571,NULL,'',NULL,23,NULL,NULL,46,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (572,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,128);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (573,NULL,'',NULL,23,NULL,NULL,46,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (574,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,129);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (575,NULL,'',NULL,23,NULL,NULL,46,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (576,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,130);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (577,NULL,'',NULL,23,NULL,NULL,46,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (578,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,131);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (579,NULL,'',NULL,23,NULL,NULL,46,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (580,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,132);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (581,NULL,'',NULL,23,NULL,NULL,46,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (582,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,133);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (583,NULL,'',NULL,23,NULL,NULL,46,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (584,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,134);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (585,NULL,'',NULL,23,NULL,NULL,46,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (586,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,135);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (587,NULL,'',NULL,23,NULL,NULL,46,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (588,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,136);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (589,NULL,'',NULL,23,NULL,NULL,46,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (590,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,137);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (591,NULL,'',NULL,23,NULL,NULL,46,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (592,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,139);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (593,NULL,'',NULL,23,NULL,NULL,46,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (594,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,140);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (595,NULL,'',NULL,23,NULL,NULL,46,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (596,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,142);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (597,NULL,'',NULL,23,NULL,NULL,46,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (598,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,123);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (599,NULL,'',NULL,23,NULL,NULL,46,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (600,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,124);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (601,NULL,'',NULL,23,NULL,NULL,46,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (602,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,125);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (603,NULL,'',NULL,23,NULL,NULL,46,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (604,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,126);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (605,NULL,'',NULL,23,NULL,NULL,46,127);
INSERT INTO `question_status` (`id`, `assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id`) VALUES (606,'2016-05-24 10:23:34',NULL,'',23,46,46,NULL,127);


INSERT INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (35,26);
INSERT INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (36,27);
INSERT INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (37,28);


INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (35,4);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (36,4);
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (37,4);

