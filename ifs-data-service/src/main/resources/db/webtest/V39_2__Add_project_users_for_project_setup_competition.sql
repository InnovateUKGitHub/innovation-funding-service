SELECT @projectId1 := id FROM project WHERE `name` = 'best riffs';
SELECT @projectId2 := id FROM project WHERE `name` = 'better riffs';
SELECT @projectId3 := id FROM project WHERE `name` = 'awesome riffs';

INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId1,31,10,40);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId2,32,10,41);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId2,32,10,42);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,33,10,43);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,33,10,44);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,34,10,45);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,34,10,46);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,35,10,47);
INSERT  IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId3,35,10,48);