-- IFS-3978: Assign a Project to a Monitoring Officer.

SET @mo_user_id =
(SELECT id FROM user WHERE email = 'Orville.Gibbs@gmail.com');

SET @mo_organisation_id =
(SELECT id FROM organisation WHERE name = 'Innovate UK');

SET @monitoring_officer_role_id =
(SELECT id FROM role WHERE name = 'monitoring_officer');

SET @assigned_project_id =
(SELECT id FROM project WHERE name = 'Magic material');

SET @assigned_application_id =
(SELECT application_id FROM project WHERE name = 'Magic material');

insert into user_organisation
values
(@mo_user_id, @mo_organisation_id);

insert into project_user
(project_id, organisation_id, user_id, project_role, participant_status_id)
values
(@assigned_project_id, @mo_organisation_id, @mo_user_id, 'MONITORING_OFFICER', 2);


insert into process_role
(application_id, organisation_id, role_id, user_id)
values
(@assigned_application_id, @mo_organisation_id, @monitoring_officer_role_id, @mo_user_id);


