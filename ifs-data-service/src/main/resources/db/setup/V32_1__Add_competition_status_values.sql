UPDATE `competition` SET `status`='COMPETITION_SETUP_FINISHED' WHERE true=true;

-- Add Competition Types
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('Programme', 1);
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('Additive Manufacturing', 1);
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('SBRI', 0);
INSERT INTO `competition_type` (`name`, `state_aid`) VALUES ('Special', 0);

-- ADD INNOVATION_SECTOR
INSERT INTO `category` (id, `name`, `type`) VALUES (1, 'Health and life sciences', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (2, 'Materials and manufacturing', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (3, 'Emerging and enabling technologies', 'INNOVATION_SECTOR');
INSERT INTO `category` (id, `name`, `type`) VALUES (4, 'Infrastructure systems', 'INNOVATION_SECTOR');

-- ADD INNOVATION_AREA
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Earth Observation', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Internet of Things', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Data', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Cyber Security', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('User Experience', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Emerging Tech and Industries', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Robotics and AS', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Electronics, Sensors and photonics', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Open', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Design', 'INNOVATION_AREA', '3');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Creative economy', 'INNOVATION_AREA', '3');

INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Advanced Therapies', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Precision Medicine', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Medicines Technology', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Bioscience', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Agri Productivity', 'INNOVATION_AREA', '1');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Enhanced Food Quality', 'INNOVATION_AREA', '1');

INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Digital Manufacturing', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Early Stage Manufacturing', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Manufacturing Readiness', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Resource efficiency', 'INNOVATION_AREA', '2');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Advanced Materials', 'INNOVATION_AREA', '2');

INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Transport Systems', 'INNOVATION_AREA', '4');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Urban living', 'INNOVATION_AREA', '4');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Infrastructure', 'INNOVATION_AREA', '4');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Energy Systems', 'INNOVATION_AREA', '4');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Nuclear', 'INNOVATION_AREA', '4');
INSERT INTO `category` (`name`, `type`, `parent_id`) VALUES ('Offshore Renewable Energy', 'INNOVATION_AREA', '4');


INSERT INTO `role` (`id`, `name`) VALUES (12, 'competition_executive');
INSERT INTO `role` (`id`, `name`) VALUES (13, 'competition_technologist');

