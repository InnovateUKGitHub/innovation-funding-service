-- IFS-7213 add INVESTOR_PARTNERSHIP funding type
ALTER TABLE competition MODIFY COLUMN funding_type enum('GRANT','LOAN','PROCUREMENT','KTP','INVESTOR_PARTNERSHIPS');

SET @system_maintenance_user_id = (
    SELECT id
    FROM user
    WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

SET @new_terms_id = 31;

INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_by, modified_on)
VALUES (@new_terms_id, 'Investor Partnerships', 'investor-partnership-terms-and-conditions', 1, 'GRANT',
        @system_maintenance_user_id, now(), @system_maintenance_user_id, now());