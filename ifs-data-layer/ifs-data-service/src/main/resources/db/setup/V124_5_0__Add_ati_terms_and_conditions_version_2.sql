-- IFS-3205: As an applicant I am able to see the current ATI grant terms and conditions

-- Add entry into terms and conditions table for the version 2 of ATI terms and conditions
SET @system_maintenance_user_id = (SELECT id
                                   FROM user
                                   WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES
  ('Aerospace Technology Institute (ATI)',
  'ati-terms-and-conditions-v2',
   2,
   'GRANT',
   @system_maintenance_user_id,
   NOW(),
   NOW(),
   @system_maintenance_user_id);

-- Update link from template ATI competition to new version of terms and conditions so these become the default Ts&Cs
--  used for new competitions of ATI type.
SET @ati_terms_and_conditions_id=(SELECT id FROM terms_and_conditions WHERE name='Aerospace Technology Institute (ATI)' AND version='2');
SET @ati_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Aerospace Technology Institute');

UPDATE competition SET terms_and_conditions_id=@ati_terms_and_conditions_id WHERE id=@ati_template_id;