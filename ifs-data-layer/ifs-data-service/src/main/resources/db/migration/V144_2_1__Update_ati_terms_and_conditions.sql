-- IFS-5980 update ati terms to point to the new template

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

UPDATE terms_and_conditions
SET
    template = 'ati-terms-and-conditions-v3',
    version = '3',
    modified_by = @system_maintenance_user_id,
    modified_on = now()
WHERE
    id = 8;