-- IFS-11299 - HECP funding type and GOL template
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @hecp_gol_template_id = 65;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (
        @hecp_gol_template_id,
        'Horizon Europe Guarantee GOL Template',
        'hecp-gol-template',
        1,
        'GOL',
        @system_maintenance_user_id,
        NOW(),
        @system_maintenance_user_id,
        now());