-- IFS-6162: Add live project user role to monitoring officers on already live projects

INSERT INTO user_role (user_id, role_id)
SELECT pu.user_id, 21
FROM project_user pu
JOIN project p ON pu.project_id = p.id
JOIN grant_process gp on p.application_id = gp.application_id
WHERE gp.sent_succeeded IS NOT NULL
AND pu.project_role = 'MONITORING_OFFICER';

