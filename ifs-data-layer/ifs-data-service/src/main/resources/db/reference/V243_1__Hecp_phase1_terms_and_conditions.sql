-- IFS-10982 - Horizon Europe Contingency Programme - T&Cs
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org'
);

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (
           'Horizon Europe Contingency Programme Privacy Notice', 'hecp-terms-and-conditions', 1, 'GRANT',
           @system_maintenance_user_id, NOW(),
           @system_maintenance_user_id, NOW());

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (
           'Horizon Europe Contingency Programme Privacy Notice', 'hecp-terms-and-conditions', 1, 'GRANT',
           @system_maintenance_user_id, NOW(),
           @system_maintenance_user_id, NOW());