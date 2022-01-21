-- IFS-1115 update iuk default terms and conditions

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @old_terms_id = 34;
SET @new_terms_id = 63; -- 64 if loans t&c goes first

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Innovate UK', 'default-terms-and-conditions-v7', 7, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());

-- -- Update link from template competition to new version of terms and conditions so these become the default Ts&Cs
-- UPDATE competition SET terms_and_conditions_id = @new_terms_id WHERE terms_and_conditions_id = @old_terms_id and template = 1;