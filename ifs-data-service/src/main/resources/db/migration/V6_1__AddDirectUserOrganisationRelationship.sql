CREATE TABLE `user_organisation` (
  `user_id` bigint(20) NOT NULL,
  `organisation_id` bigint(20) NOT NULL,
  KEY `FK_hovbl4knvvbdxktjlkkxnbuh0` (`organisation_id`),
  KEY `FK_kbg0lkwwyivtraa6pm155q9lb` (`user_id`),
  CONSTRAINT `FK_kbg0lkwwyivtraa6pm155q9lb` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_hovbl4knvvbdxktjlkkxnbuh0` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `user_organisation` WRITE;
INSERT  IGNORE INTO `user_organisation` (`user_id`, `organisation_id`) VALUES (1,3),(2,4),(3,2),(8,6),(9,2);
UNLOCK TABLES;

