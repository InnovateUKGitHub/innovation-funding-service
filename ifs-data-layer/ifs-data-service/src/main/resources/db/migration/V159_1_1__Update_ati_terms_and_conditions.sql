-- IFS-7081 - Update ati terms and conditions to be applicable to new competitions

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 26;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Aerospace Technology Institute (ATI)', 'ati-terms-and-conditions-v4', 4, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());

-- update the ati template competition to point to the new t&cs
UPDATE competition c
SET terms_and_conditions_id = @new_terms_id
WHERE
c.name = 'Template for the Aerospace Technology Institute competition type' AND c.template;