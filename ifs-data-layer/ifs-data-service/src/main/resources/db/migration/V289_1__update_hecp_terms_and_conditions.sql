-- IFS-13198 - Horizon Europe Terms and conditions V3- T&Cs
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org'
);

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES ('Horizon Europe Guarantee', 'horizon-europe-guarantee-terms-and-conditions-v3', 3, 'GRANT',
        @system_maintenance_user_id, NOW(),
        @system_maintenance_user_id, NOW());