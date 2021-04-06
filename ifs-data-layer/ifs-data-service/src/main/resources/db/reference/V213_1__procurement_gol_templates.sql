SET @system_maintenance_user_id = (
SELECT id
FROM user
WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @procurement_gol_template_id = 51;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@procurement_gol_template_id, 'Procurement GOL Template', 'procurement-gol-template', 1, 'GOL',
@system_maintenance_user_id, now(), @system_maintenance_user_id, now());
