CREATE TABLE `public_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(20) NOT NULL,
  `publish_date` datetime,
  `short_description` varchar(255),
  `project_funding_range`varchar(255),
  `eligibility_summary` varchar(255),
  `summary` longtext,
  `funding_type` ENUM('GRANT', 'PROCUREMENT'),
  `project_size` varchar(255),
  PRIMARY KEY (`id`),
  KEY `FK_public_content_to_competition` (`competition_id`),
  CONSTRAINT `FK_public_content_to_competition` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `keyword` varchar(50),
  PRIMARY KEY (`id`),
  KEY `FK_keyword_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_keyword_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `content_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `date` datetime,
  `content` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_content_event_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_content_event_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `content_section` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `public_content_id` bigint(20) NOT NULL,
  `status` ENUM('IN_PROGRESS', 'COMPLETE'),
  `type` ENUM('SEARCH', 'SUMMARY', 'ELIGIBILITY', 'SCOPE', 'DATES', 'HOW_TO_APPLY', 'SUPPORTING_INFORMATION'),
  PRIMARY KEY (`id`),
  KEY `FK_content_section_to_public_content` (`public_content_id`),
  CONSTRAINT `FK_content_section_to_public_content` FOREIGN KEY (`public_content_id`) REFERENCES `public_content` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `content_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content_section_id` bigint(20) NOT NULL,
  `heading` varchar(255),
  `content` longtext,
  `file_entry_id` bigint(20),
  `priority` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_content_group_to_content_section` (`content_section_id`),
  KEY `FK_content_group_to_file_entry` (`file_entry_id`),
  CONSTRAINT `FK_content_group_to_content_section` FOREIGN KEY (`content_section_id`) REFERENCES `content_section` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_content_group_to_file_entry` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
