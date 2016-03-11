SET SQL_SAFE_UPDATES = 0;
ALTER TABLE `user` ADD COLUMN `status` VARCHAR(255) NULL DEFAULT NULL COMMENT '';
UPDATE INGORE `user` SET `status`='ACTIVE';
SET SQL_SAFE_UPDATES = 1;


CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) DEFAULT NULL,
  `class_pk` bigint(20) DEFAULT NULL,
  `extra_info` longtext,
  `hash` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mtjr5jkw9dpbqhgx3mu1bomlt` (`hash`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
