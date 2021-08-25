SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES ('Innovate UK - Subsidy control', 'default-subsidy-control-terms-and-conditions-v5', 5, 'GRANT', @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id);