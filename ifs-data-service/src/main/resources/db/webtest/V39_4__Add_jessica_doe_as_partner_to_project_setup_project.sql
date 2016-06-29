SELECT @leadApplicantRoleId := id FROM role WHERE `name` = 'leadapplicant';
SELECT @collaboratorRoleId := id FROM role WHERE `name` = 'collaborator';
SELECT @partnerRoleId := id FROM role WHERE `name` = 'partner';

SELECT @projectId := id FROM project WHERE `name` = 'best riffs';
SELECT @applicationId := application_id FROM project WHERE id = @projectId;
SELECT @jessicaDoeId := id FROM `user` WHERE `email` = 'jessica.doe@ludlow.co.uk';
SELECT @ludlowOrganisationId := id FROM organisation where name = 'Ludlow';

-- Make Jessica Doe a collaborator on "best riffs"
INSERT IGNORE INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@applicationId, @ludlowOrganisationId, @collaboratorRoleId, @jessicaDoeId);

-- Make Jessica Doe a partner on "best riffs"
INSERT IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId, @ludlowOrganisationId, @partnerRoleId, @jessicaDoeId);