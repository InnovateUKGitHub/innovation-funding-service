UPDATE `organisation` SET `name`='Vitruvius Stonework Limited', `company_house_number`='60674010' WHERE `id`='31';

SELECT @leadApplicantRoleId := id FROM role WHERE `name` = 'leadapplicant';
SELECT @collaboratorRoleId := id FROM role WHERE `name` = 'collaborator';
SELECT @partnerRoleId := id FROM role WHERE `name` = 'partner';

SELECT @compId := id FROM competition WHERE `name` = 'Killer Riffs';
SELECT @projectId := id FROM project WHERE `name` = 'best riffs';
SELECT @applicationId := application_id FROM project WHERE id = @projectId;
SELECT @peteTomId:= id FROM `user` WHERE `email` = 'pete.tom@egg.com';
SELECT @eggsOrganisationId := id FROM organisation where name = 'EGGS';

-- Make EGGS organisation

-- Make Pete Tom a collaborator on "best riffs"
INSERT IGNORE INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@applicationId, @eggsOrganisationId, @collaboratorRoleId, @peteTomId);

-- Make Pete Tom a partner on "best riffs"
INSERT IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId, @eggsOrganisationId, @partnerRoleId, @peteTomId);

-- Insert missing application finance for new organisation(s) now added to project in steps above.
-- NOTE: This is just re-execution of V39_5 because we are adding another org here.
INSERT IGNORE INTO application_finance (application_id, organisation_id, organisation_size, finance_file_entry_id)
  SELECT a.id, pu.organisation_id, 'SMALL', null FROM application a
    JOIN project proj ON proj.application_id = a.id
    JOIN project_user pu ON pu.project_id = proj.id
  WHERE NOT EXISTS (
      SELECT 1 FROM application_finance a2
      WHERE a2.application_id = a.id AND a2.organisation_id = pu.organisation_id
  );

UPDATE `competition` SET `academic_grant_percentage`='100' WHERE `id`= @compId;