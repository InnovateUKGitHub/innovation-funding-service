--
-- Link existing competitions (and competition templates) with their appropriate terms and conditions
--
SET @default_terms_and_conditions_id=(SELECT id FROM terms_and_conditions where name = 'Default Terms and Conditions');
SET @apc_terms_and_conditions_id=(SELECT id FROM terms_and_conditions where name = 'APC Terms and Conditions');
SET @ati_terms_and_conditions_id=(SELECT id FROM terms_and_conditions where name = 'ATI Terms and Conditions');

SET @apc_competition_type_id=(SELECT id FROM competition_type WHERE name='Advanced Propulsion Centre');
SET @ati_competition_type_id=(SELECT id FROM competition_type WHERE name='Aerospace Technology Institute');

UPDATE competition SET terms_and_conditions_id=@apc_terms_and_conditions_id
  WHERE competition_type_id=@apc_competition_type_id;

UPDATE competition SET terms_and_conditions_id=@ati_terms_and_conditions_id
  WHERE competition_type_id=@ati_competition_type_id;

UPDATE competition SET terms_and_conditions_id=@apc_terms_and_conditions_id
  WHERE name = 'Template for the Advanced Propulsion Centre competition type';

UPDATE competition SET terms_and_conditions_id=@ati_terms_and_conditions_id
  WHERE name = 'Template for the Aerospace Technology Institute competition type';