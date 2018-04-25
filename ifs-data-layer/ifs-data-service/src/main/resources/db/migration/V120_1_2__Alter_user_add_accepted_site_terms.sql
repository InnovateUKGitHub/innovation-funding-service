--IFS-3093 Add column to store the id of the accepted site terms and conditions
ALTER TABLE user
  ADD COLUMN site_terms_and_conditions_id bigint(20),
  ADD COLUMN site_terms_and_conditions_accepted datetime;