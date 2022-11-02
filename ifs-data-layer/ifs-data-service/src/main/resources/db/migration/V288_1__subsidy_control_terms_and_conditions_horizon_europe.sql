-- IFS-13212 - Subsidy control T&Cs for Horizon Europe

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 75;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Horizon Europe Guarantee - Subsidy control', 'horizon-europe-guarantee-subsidy-control-terms-and-conditions-v1', 1, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());
