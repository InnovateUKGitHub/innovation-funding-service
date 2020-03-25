-- IFS-7205 add sweden bilateral aerospace terms and conditions
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 29;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Aerospace Technology Institute (ATI) EUREKA UK-Sweden', 'ati-sweden-terms-and-conditions', 1, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());
