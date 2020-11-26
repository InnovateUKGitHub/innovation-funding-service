-- IFS-8638 - HEUKAR - T&Cs
SET @system_maintenance_user_id = (
SELECT id
FROM user
WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES ('Heukar', 'heukar-terms-and-conditions', 1, 'GRANT',
@system_maintenance_user_id, now(), @system_maintenance_user_id, now());