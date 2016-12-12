-- Create new profiles
INSERT INTO `profile` VALUES (4,96,NULL,NULL,NULL,NULL,15,'2016-12-09 09:59:17',15,'2016-12-09 09:59:17');
INSERT INTO `profile` VALUES (5,96,NULL,NULL,NULL,NULL,15,'2016-12-09 09:59:17',15,'2016-12-09 09:59:17');
INSERT INTO `profile` VALUES (6,96,NULL,NULL,NULL,NULL,15,'2016-12-09 09:59:17',15,'2016-12-09 09:59:17');
INSERT INTO `profile` VALUES (7,96,NULL,NULL,NULL,NULL,15,'2016-12-09 09:59:17',15,'2016-12-09 09:59:17');

-- Insert new users
INSERT INTO `user` VALUES (80,'henry.jones@gmail.com',NULL,'Henry',NULL,'Jones','123456789',NULL,'ACTIVE','b9d7fcd3-d56b-4828-a313-8b8008155ccf',0,'MALE','NO',1,4);
INSERT INTO `user` VALUES (81,'graham.philips@gmail.com',NULL,'Graham',NULL,'Philips','987654321',NULL,'ACTIVE','b9d7fcd3-d56b-4828-a313-8b8008155ccf',0,'MALE','NO',2,5);
INSERT INTO `user` VALUES (82,'jane.robbins@gmail.com',NULL,'Jane',NULL,'Robbins','098765678',NULL,'ACTIVE','b9d7fcd3-d56b-4828-a313-8b8008155ccf',0,'FEMALE','NO',3,6);
INSERT INTO `user` VALUES (83,'vanessa.kensington@gmail.com',NULL,'Vanessa',NULL,'Kensington','09754347',NULL,'ACTIVE','b9d7fcd3-d56b-4828-a313-8b8008155ccf',0,'FEMALE','NO',4,7);

-- Insert users as assessors
INSERT INTO `user_role` VALUES (80,3);
INSERT INTO `user_role` VALUES (81,3);
INSERT INTO `user_role` VALUES (82,3);
INSERT INTO `user_role` VALUES (83,3);
