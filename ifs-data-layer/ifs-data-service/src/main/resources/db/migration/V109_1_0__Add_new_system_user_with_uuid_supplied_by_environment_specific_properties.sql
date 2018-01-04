/*
- Purpose of this patch is to insert a new system user (for access to registration and other anonymous features), with environment specific uuid (specified via build-docker.gradle or overridden by bamboo job).
- In order to allow for ZDD deployment, this user will be added temporarily and then original system user 'ifs_web_user_1@innovateuk.org' will be deleted and this new user's email be updated to ifs_web_user@innovateuk.org in a follow up ticket.
*/

SELECT id INTO @sys_maintenance_user_id FROM user WHERE email='ifs_system_maintenance_user@innovateuk.org';

INSERT INTO `user` (email, first_name, last_name, status, uid, system_user, created_by, created_on, modified_by, modified_on) VALUES('ifs_web_user_1@innovateuk.org', 'IFS Web', 'System User', 'ACTIVE', '${ifs.system.user.uuid}', 1, @sys_maintenance_user_id, NOW(), @sys_maintenance_user_id, NOW());

INSERT INTO user_role (user_id, role_id) SELECT u.id, r.id FROM `user` u, `role` r where u.email = 'ifs_web_user_1@innovateuk.org' AND r.name = 'system_registrar';