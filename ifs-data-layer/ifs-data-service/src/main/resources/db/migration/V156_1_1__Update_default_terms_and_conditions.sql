-- IFS-6931 update ati terms to point to the new terms template

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 25;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Innovate UK', 'default-terms-and-conditions-v4', 4, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());