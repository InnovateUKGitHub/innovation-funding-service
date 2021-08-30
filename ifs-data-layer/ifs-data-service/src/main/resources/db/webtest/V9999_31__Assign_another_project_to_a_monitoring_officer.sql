-- IFS-10287: Assign a Project to a Monitoring Officer.

SET @mo_user_id =
(SELECT id FROM user WHERE email = 'orville.Gibbs@gmail.com');

SET @mo_organisation_id =
(SELECT id FROM organisation WHERE name = 'Innovate UK');

SET @monitoring_officer_role_id =
(SELECT id FROM role WHERE name = 'monitoring_officer');

SET @assigned_project_id =
(SELECT id FROM project WHERE name = 'Monitoring Officer - GOL access');

SET @assigned_application_id =
(SELECT application_id FROM project WHERE name = 'Monitoring Officer - GOL access');

INSERT INTO project_user
(project_id, organisation_id, user_id, project_role, participant_status_id, type)
VALUES
(@assigned_project_id, @mo_organisation_id, @mo_user_id, 'MONITORING_OFFICER', 2, 'PROJECT_MONITORING_OFFICER');