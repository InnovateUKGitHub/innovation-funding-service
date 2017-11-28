-- add project partner user with lead role on application
SELECT id INTO @app_id FROM application WHERE name ='App21007';
SELECT id INTO @user_id FROM user WHERE email = 'pmifs2100@gmail.vom';
SELECT id INTO @project_id FROM project WHERE name = 'project 8';

INSERT INTO project_user (project_id, user_id, project_role, participant_status_id) VALUES(@project_id, @user_id, 'PROJECT_PARTNER', 2);
INSERT INTO process_role (application_id, user_id, role_id) VALUES(@app_id, @user_id, 1);