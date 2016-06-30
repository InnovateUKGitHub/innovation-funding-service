SELECT @leadApplicantRoleId := id FROM role WHERE `name` = 'leadapplicant';
SELECT @collaboratorRoleId := id FROM role WHERE `name` = 'collaborator';
SELECT @partnerRoleId := id FROM role WHERE `name` = 'partner';

SELECT @projectId := id FROM project WHERE `name` = 'best riffs';
SELECT @applicationId := application_id FROM project WHERE id = @projectId;
SELECT @steveSmithId := id FROM `user` WHERE `email` = 'steve.smith@empire.com';
SELECT @leadApplicantProcessRoleId := id FROM process_role WHERE role_id = @leadApplicantRoleId AND application_id = @applicationId;
SELECT @leadOrganisationId := organisation_id FROM process_role where id = @leadApplicantProcessRoleId;
SELECT @currentLeadApplicantUserId := user_id FROM process_role where id = @leadApplicantProcessRoleId;

-- Make Steve Smith the Lead Applicant on "best riffs"
UPDATE process_role SET user_id = @steveSmithId where id = @leadApplicantProcessRoleId;

-- Make the original Lead Applicant a simple Collaborator on "best riffs"
INSERT IGNORE INTO `process_role` (`application_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@applicationId, @leadOrganisationId, @collaboratorRoleId, @currentLeadApplicantUserId);

-- Make Steve Smith a Lead Partner on "best riffs"
INSERT IGNORE INTO `project_user` (`project_id`, `organisation_id`, `role_id`, `user_id`) VALUES (@projectId, @leadOrganisationId, @partnerRoleId, @steveSmithId);