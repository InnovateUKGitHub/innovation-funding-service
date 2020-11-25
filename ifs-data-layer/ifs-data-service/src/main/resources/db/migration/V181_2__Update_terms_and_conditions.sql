-- IFS-8743 of terms and conditions for innovate uk, ATI, APC and new projects temporary framework

SET @system_maintenance_user_id =
(SELECT id FROM user WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

INSERT INTO terms_and_conditions (name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES ("Innovate UK", "default-terms-and-conditions-v6", 6, "GRANT", @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id),
("Aerospace Technology Institute (ATI)", "ati-terms-and-conditions-v5", 5, "GRANT", @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id),
("Advanced Propulsion Centre (APC)", "apc-terms-and-conditions-v3", 3, "GRANT", @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id),
("New projects temporary framework", "new-projects-temporary-terms-and-conditions-v2", 2, "GRANT", @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id);