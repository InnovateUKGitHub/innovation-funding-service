-- IFS-10325 ktp subsidy control t&cs update

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES ('Knowledge Transfer Partnership (KTP) - Subsidy control', 'ktp-subsidy-control-terms-and-conditions-v2', 2, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());
