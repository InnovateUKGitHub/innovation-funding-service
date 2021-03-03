-- Delete project finance role from IFS_ADMIN users.

DELETE ur FROM user_role ur
INNER JOIN user_role inner_ur ON inner_ur.user_id = ur.user_id AND inner_ur.role_id = 14 -- ifs admin
WHERE ur.role_id = 8; -- project finance