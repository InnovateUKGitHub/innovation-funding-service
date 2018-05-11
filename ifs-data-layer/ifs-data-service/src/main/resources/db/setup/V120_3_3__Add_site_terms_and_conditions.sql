--IFS-3093 Add site terms and conditions entry for the current version of the site terms and conditions

SET @system_maintenance_user_id = (SELECT id
                                   FROM user
                                   WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES
  ('Site Terms and Conditions',
   'terms-and-conditions',
   1,
   'SITE',
   @system_maintenance_user_id,
   NOW(),
   NOW(),
   @system_maintenance_user_id);