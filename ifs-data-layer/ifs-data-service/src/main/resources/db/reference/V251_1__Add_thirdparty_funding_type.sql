ALTER TABLE competition MODIFY COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT','KTP','INVESTOR_PARTNERSHIPS','HECP', 'THIRDPARTY');

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @thirdparty_gol_template_id = 65;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@thirdparty_gol_template_id,
        'Procurement Third Party',
        'third-party-terms-and-conditions',
        1,
        'GOL',
        @system_maintenance_user_id,
        NOW(),
        @system_maintenance_user_id,
        now());