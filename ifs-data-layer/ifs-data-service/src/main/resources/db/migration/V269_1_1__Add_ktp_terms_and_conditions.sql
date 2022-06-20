SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 69;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_on, modified_by)
values(@new_terms_id, 'Knowledge Transfer Partnership (KTP)', 'ktp-terms-and-conditions-v2', 2, 'GRANT', @system_maintenance_user_id, now(), now(), @system_maintenance_user_id);
