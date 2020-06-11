-- IFS-7629 add new projects temporary terms and conditions
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 30;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'New projects temporary framework', 'new-projects-temporary-terms-and-conditions', 1, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());
