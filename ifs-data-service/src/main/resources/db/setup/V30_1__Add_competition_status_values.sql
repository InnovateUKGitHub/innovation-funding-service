UPDATE `competition` SET `status`='COMPETITION_SETUP_FINISHED' WHERE true=true;

-- Add Competition Types
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('Technology Inspired', 1);
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('Additive Manufacturing', 0);

-- ADD INNOVATION_SECTOR
INSERT INTO `category` (id, `name`, `type`) VALUES (1, 'Health and life sciences', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (2, 'Materials and manufacturing', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (3, 'Emerging and enabling', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (4, 'Infrastructure', 'INNOVATION_SECTOR');

-- ADD INNOVATION_AREA
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Transport', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Energy', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Urban living and built', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Health and care', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Agriculture and food', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Biosciences', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('High value manufacturing', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Resource efficiency', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Advanced materials', 'INNOVATION_AREA', '2');

-- ADD Competition Setup Sections
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (1, 'Initial details', 'initial-details', '1');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (2, 'Additional info', 'additional-info', '2');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (3, 'Eligibility', 'eligibility', '3');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (4, 'Milestones', 'milestones', '4');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (5, 'Assessors', 'assessors', '5');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (6, 'Application form', 'application-form', '6');
INSERT INTO `competition_setup_section` (`id`, `name`, `path`, `priority`) VALUES (7, 'Finance', 'finance', '7');


INSERT INTO `role` (`id`, `name`) VALUES (9, 'competition_executive');
INSERT INTO `role` (`id`, `name`) VALUES (10, 'competition_technologist');

