SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 66;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_on, modified_by)
values(new_terms_id, 'BEIS Net Zero', 'hydrogen-terms-and-conditions-v1', 1, 'GRANT', @system_maintenance_user_id, now(), now(), @system_maintenance_user_id);