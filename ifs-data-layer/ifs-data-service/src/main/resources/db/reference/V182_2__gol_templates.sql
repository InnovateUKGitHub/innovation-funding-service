SET @system_maintenance_user_id = (
SELECT id
FROM user
WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

-- TODO check these ids
SET @default_gol_template_id = 50;
SET @KTP_gol_template_id = 51;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@default_gol_template_id, 'Default GOL Template', 'default-gol-template', 1, 'GOL',
@system_maintenance_user_id, now(), @system_maintenance_user_id, now());

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@KTP_gol_template_id, 'KTP GOL Template', 'ktp-gol-template', 1, 'GOL',
@system_maintenance_user_id, now(), @system_maintenance_user_id, now());