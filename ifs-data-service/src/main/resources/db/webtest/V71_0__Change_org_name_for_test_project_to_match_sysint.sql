UPDATE `organisation` SET `name`='Vitruvius Stonework Limited', `company_house_number`='60674010' WHERE `id`='31';

SELECT @leadApplicantRoleId := id FROM role WHERE `name` = 'leadapplicant';
SELECT @collaboratorRoleId := id FROM role WHERE `name` = 'collaborator';
SELECT @partnerRoleId := id FROM role WHERE `name` = 'partner';

SELECT @projectId := id FROM project WHERE `name` = 'best riffs';
SELECT @applicationId := application_id FROM project WHERE id = @projectId;
SELECT @peteTomId:= id FROM `user` WHERE `email` = 'pete.tom@egg.com';
SELECT @eggsOrganisationId := id FROM organisation where name = 'EGGS';

-- Make Pete Tom a collaborator on "best riffs"
INSERT IGNORE INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@applicationId, @eggsOrganisationId, @collaboratorRoleId, @peteTomId);

-- Make Pete Tom a partner on "best riffs"
INSERT IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId, @eggsOrganisationId, @partnerRoleId, @peteTomId);