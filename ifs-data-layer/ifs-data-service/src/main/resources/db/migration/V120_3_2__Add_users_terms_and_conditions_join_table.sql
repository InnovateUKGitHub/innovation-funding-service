--IFS-3093 Create a many-to-many relationship between user and terms_and_conditions for storing the acceptance of site terms and conditions
CREATE TABLE `user_terms_and_conditions` (
  `user_id` bigint(20) NOT NULL,
  `terms_and_conditions_id` bigint(20) NOT NULL,
  `accepted_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`terms_and_conditions_id`),
  CONSTRAINT `user_terms_and_conditions_terms_and_conditions_id_to_terms_fk` FOREIGN KEY (`terms_and_conditions_id`) REFERENCES `terms_and_conditions` (`id`),
  CONSTRAINT `user_terms_and_conditions_user_id_to_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;