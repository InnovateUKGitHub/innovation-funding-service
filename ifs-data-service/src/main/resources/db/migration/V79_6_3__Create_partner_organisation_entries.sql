INSERT INTO partner_organisation (project_id, organisation_id, lead_organisation)
  SELECT pr.id, pu.organisation_id, TRUE
  FROM project pr
  JOIN project_user pu ON pu.project_id = pr.id
                       AND pu.project_role = 'PROJECT_PARTNER'
  JOIN application app ON app.id = pr.application_id
  JOIN process_role prole ON prole.application_id = app.id
                          AND prole.organisation_id = pu.organisation_id
                          AND prole.user_id = pu.user_id
                          AND prole.role_id = 1;


INSERT INTO partner_organisation (organisation_id, project_id, lead_organisation)
  SELECT DISTINCT(pu.organisation_id), pr.id, FALSE
  FROM project pr
  JOIN project_user pu ON pu.project_id = pr.id
                       AND pu.project_role = 'PROJECT_PARTNER'
  JOIN application app ON app.id = pr.application_id
  JOIN process_role prole ON prole.application_id = app.id
                          AND prole.organisation_id = pu.organisation_id
                          AND prole.user_id = pu.user_id
                          AND prole.role_id = 2
  WHERE NOT EXISTS (SELECT 1 FROM partner_organisation WHERE project_id = pr.id AND organisation_id = pu.organisation_id);