-- IFS-11911 Innovate UK T&C update

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES ('CCAV - ZENZIC – Subsidy control', 'zenzic-terms-and-conditions', 1, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());