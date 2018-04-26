--IFS-3093 Add columns for storing the acceptance of site terms and conditions
ALTER TABLE profile
  ADD COLUMN site_terms_and_conditions_id bigint(20),
  ADD COLUMN site_terms_and_conditions_accepted_date datetime,
  ADD CONSTRAINT `profile_site_terms_and_conditions_id_to_terms_and_conditions_fk` FOREIGN KEY (`site_terms_and_conditions_id`) REFERENCES `terms_and_conditions` (`id`);