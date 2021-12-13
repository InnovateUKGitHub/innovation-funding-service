CREATE TABLE `invite_history`
(
    `id`         bigint(20)                       NOT NULL AUTO_INCREMENT,
    `invite_id`  bigint(20)                       NOT NULL,
    `status`     enum ('SENT','CREATED','OPENED') NOT NULL,
    `updated_on` datetime   DEFAULT NULL,
    `updated_by` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_keyword_to_public_content` (`invite_id`),
    CONSTRAINT `FK_invite_id` FOREIGN KEY (`invite_id`) REFERENCES `invite` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION

) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8;