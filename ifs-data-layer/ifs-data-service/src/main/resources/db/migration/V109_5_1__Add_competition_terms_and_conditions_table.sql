--
-- Table structure for table `competition_terms_and_conditions`
--
CREATE TABLE `competition_terms_and_conditions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `template` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `version` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `competition_terms_and_conditions_UNIQUE` (`name`, `version`)
) ENGINE=InnoDB AUTO_INCREMENT=1