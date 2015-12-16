UPDATE `question_status` SET `marked_as_complete`=NULL, `notified`=1, `assigned_by_id`='1', `assignee_id`='1' WHERE `id`='6';
UPDATE `question_status` SET `marked_as_complete`=NULL, `notified`=1, `assigned_by_id`='1', `assignee_id`='1', `marked_as_complete_by_id`=NULL WHERE `id`='2';
UPDATE `ifs`.`question_status` SET `assignee_id`='1' WHERE `id`='9';

INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '1');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '2');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '3');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '5');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '6');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '7');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '8');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '12');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '15');
INSERT INTO `question_status` (`application_id`, `assigned_by_id`, `assignee_id`, `question_id`) VALUES ('1', '1', '1', '16');

