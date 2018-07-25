-- IFS-3980
-- New version of site-wide terms and conditions to include GDPR notice

SET @system_maintenance_user_id =
(SELECT id FROM user WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES ("Site Terms and Conditions", "terms-and-conditions-v2", 2, "SITE", @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id);