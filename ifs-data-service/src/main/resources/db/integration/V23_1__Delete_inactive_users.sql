-- Delete users ewan+2@hiveit.co.uk and ewan+12@hiveit.co.uk which are inactive and not used
DELETE FROM `token` WHERE `class_name` = 'com.worth.ifs.user.domain.User' AND `class_pk` IN (11, 12);
DELETE FROM `user_role` WHERE `user_id` IN (11, 12);
DELETE FROM `process_role` WHERE `user_id` IN (11, 12);
DELETE FROM `user_organisation` WHERE `user_id` IN (11, 12);
DELETE FROM `user` WHERE `id` IN (11, 12);