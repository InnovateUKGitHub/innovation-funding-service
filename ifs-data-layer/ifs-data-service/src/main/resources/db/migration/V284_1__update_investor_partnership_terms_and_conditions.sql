-- IFS-12827 - Investor Partnership - Terms and conditions V2
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org'
);

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES ('Investor Partnerships', 'investor-partnership-terms-and-conditions-v2', 2, 'GRANT',
        @system_maintenance_user_id, NOW(),
        @system_maintenance_user_id, NOW());