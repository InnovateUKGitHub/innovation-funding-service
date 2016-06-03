

insert into competition (id, competition_type_id, status) values (7, 1, 'COMPETITION_SETUP');


INSERT INTO `category_link` (`id`, `class_name`, `class_pk`, `category_id`) VALUES ('1', 'com.worth.ifs.competition.domain.Competition', '7', '1');
INSERT INTO `category_link` (`id`, `class_name`, `class_pk`, `category_id`) VALUES ('2', 'com.worth.ifs.competition.domain.Competition', '7', '9');


INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Opening date', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Briefing event', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Submission date', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Allocate assessors', '7');
INSERT INTO `milestone` (`id`, `name`, `competition_id`) VALUES (NULL, 'Assessor briefing', '7');
