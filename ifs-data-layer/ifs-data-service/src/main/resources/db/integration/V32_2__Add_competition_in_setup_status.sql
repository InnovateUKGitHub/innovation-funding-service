

insert into competition (id, competition_type_id, status) values (7, 1, 'COMPETITION_SETUP');


INSERT INTO `category_link` (`id`, `class_name`, `class_pk`, `category_id`) VALUES ('1', 'com.worth.ifs.competition.domain.Competition', '7', '1');
INSERT INTO `category_link` (`id`, `class_name`, `class_pk`, `category_id`) VALUES ('2', 'com.worth.ifs.competition.domain.Competition', '7', '9');


INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Opening date', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Briefing event', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Submission date', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Allocate assessors', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Assessor briefing', '7');


-- Roles should have been added, now add the users
INSERT INTO `user` (`id`, `email`, `first_name`, `last_name`, `title`, `status`, `uid`, `system_user`) VALUES (49, 'comp_exec1@innovateuk.test', 'Competition', 'Executive One', 'Mr', 'ACTIVE', '', '0');
INSERT INTO `user` (`id`, `email`, `first_name`, `last_name`, `title`, `status`, `uid`, `system_user`) VALUES (50, 'comp_exec2@innovateuk.test', 'Competition', 'Executive Two', 'Mr', 'ACTIVE', '', '0');
INSERT INTO `user` (`id`, `email`, `first_name`, `last_name`, `title`, `status`, `uid`, `system_user`) VALUES (51, 'comp_technologist1@innovateuk.test', 'Competition', 'Technologist One', 'Mr', 'ACTIVE', '', '0');
INSERT INTO `user` (`id`, `email`, `first_name`, `last_name`, `title`, `status`, `uid`, `system_user`) VALUES (52, 'comp_technologist2@innovateuk.test', 'Competition', 'Technologist Two', 'Mr', 'ACTIVE', '', '0');

-- Add user roles
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('49', '12');
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('50', '12');
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('51', '13');
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES ('52', '13');
