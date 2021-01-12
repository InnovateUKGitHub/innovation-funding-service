SET @new_terms_id = 43;
SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Innovation Continuity Loan', 'innovation-continuity-loan-v2', 2, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());
