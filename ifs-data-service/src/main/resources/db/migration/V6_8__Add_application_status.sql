SET SQL_SAFE_UPDATES = 0;
INSERT INTO `application_status` (`id`, `name`) VALUES ('5', 'open');
UPDATE `application` SET `application_status_id`='5' where `application_status_id`='1';

SET SQL_SAFE_UPDATES = 1;