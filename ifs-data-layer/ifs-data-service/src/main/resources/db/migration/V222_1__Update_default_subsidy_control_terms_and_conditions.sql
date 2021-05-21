SET @new_terms_id = 53;
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Innovate UK - Subsidy control', 'default-subsidy-control-terms-and-conditions-v3', 3, 'GRANT', @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id);