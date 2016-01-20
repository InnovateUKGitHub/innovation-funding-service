UPDATE `question` SET `mark_as_completed_enabled`=0 WHERE `id`='40';


INSERT INTO `application` VALUES (7,23,'Marking it as complete','2015-11-01',1,1);
INSERT INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (10, '7', '3', 'SMALL');
INSERT INTO `application_finance` (`id`, `application_id`, `organisation_id`, `organisation_size`) VALUES (11, '7', '4', 'SMALL');

INSERT INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (7,3,1,1), (7,4,2,2);

INSERT INTO `question_status` (`assigned_date`, `marked_as_complete`, `notified`, `application_id`, `assigned_by_id`, `assignee_id`, `marked_as_complete_by_id`, `question_id` )
    VALUES (NOW(), 1, 0, '7', NULL, NULL, '1', '9'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '11'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '12'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '13'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '1'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '2'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '3'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '4'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '5'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '6'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '7'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '8'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '15'),
    (NOW(), 1, 0, '7', NULL, NULL, '1', '16'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '28'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '28'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '29'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '29'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '30'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '30'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '31'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '31'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '32'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '32'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '33'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '33'),
    (NOW(), 1, 0, '7', NULL, NULL, '26', '34'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '34'),

    (NOW(), 1, 0, '7', NULL, NULL, '26', '35'),
    (NOW(), 1, 0, '7', NULL, NULL, '27', '35')
    ;




INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES (NULL,'Working days per year',NULL,'100','10','28');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES (NULL,'Working days per year',NULL,'10','11','28');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('100','','Raw materials','100','10','30');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('1000','','Technician','100','10','28');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('1000','','Research engineer','100','11','28');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','Machining of parts','East engineering','0','11','32');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('100','','Plates','100','10','30');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('0','Grant Claim','','70','10','38');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('0','Grant Claim','','70','11','38');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('0','Other Funding','Yes',NULL,'10','35');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('1000','','','1000','10','28');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('1000','','','10','10','30');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('20000','','','0','10','31');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','10','32');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10','','','1000','10','33');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','10','35');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','10','34');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('20000','','','0','11','31');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','11','32');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('100','','','100','11','33');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','11','35');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','11','34');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('1','','','10000','11','33');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','11','34');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ('10000','','','0','11','35');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ( '0', 'Accept Rate', 'Yes', '23', '10', '29');
INSERT INTO `cost` (`cost`, `description`, `item`, `quantity`, `application_finance_id`, `question_id`) VALUES ( '0', 'Accept Rate', 'Yes', '24', '11', '29');

--INSERT INTO `cost_value` VALUES (69,8,10000);
--INSERT INTO `cost_value` VALUES (74,8,10000);
--INSERT INTO `cost_value` VALUES (79,8,10000);
--INSERT INTO `cost_value` VALUES (69,9,100);
--INSERT INTO `cost_value` VALUES (74,9,100);
--INSERT INTO `cost_value` VALUES (79,9,100);
