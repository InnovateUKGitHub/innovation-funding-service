-- IFS-7085 add row for all existing assessors

SET @system_user = (SELECT id FROM user WHERE email = "ifs_system_maintenance_user@innovateuk.org");

INSERT INTO role_profile_status (user_id,role_profile_state,profile_role,description,created_by,created_on,modified_by,modified_on)
SELECT u.id AS user_id,
'ACTIVE' AS role_profile_state,
'ASSESSOR' AS profile_role,
null AS description,
 @system_user AS created_by,
now() AS created_on,
 @system_user AS modified_by,
 now() AS modified_on
FROM user u
INNER JOIN user_role ur ON ur.user_id = u.id
WHERE ur.role_id = 3
AND u.id NOT IN (SELECT user_id from role_profile_status);