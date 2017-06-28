SELECT @projectManagerRoleId := id FROM role WHERE `name` = 'project_manager';

INSERT INTO project_user (project_id, organisation_id, role_id, user_id)
    SELECT p.id, pr.organisation_id, @projectManagerRoleId, pr.user_id
      FROM project p
      JOIN process_role pr ON pr.id = p.project_manager;
