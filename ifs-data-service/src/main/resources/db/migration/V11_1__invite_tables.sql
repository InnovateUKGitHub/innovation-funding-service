CREATE TABLE `invite_organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `organisation_name` varchar(255) DEFAULT NULL,
  `organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ae3mvog2j5kdcilv57hwcokmr` (`organisation_id`),
  CONSTRAINT `FK_ae3mvog2j5kdcilv57hwcokmr` FOREIGN KEY (`organisation_id`) REFERENCES `organisation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `invite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `application_id` bigint(20) DEFAULT NULL,
  `invite_organisation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_skcmllljwagey78x7lmt2n00c` (`application_id`),
  KEY `FK_hexhehvongoy5cqgpem81xs86` (`invite_organisation_id`),
  CONSTRAINT `FK_hexhehvongoy5cqgpem81xs86` FOREIGN KEY (`invite_organisation_id`) REFERENCES `invite_organisation` (`id`),
  CONSTRAINT `FK_skcmllljwagey78x7lmt2n00c` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;