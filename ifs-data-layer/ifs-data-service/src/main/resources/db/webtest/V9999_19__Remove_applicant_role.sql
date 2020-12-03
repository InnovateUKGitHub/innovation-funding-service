-- TODO Configure user builder to register stakeholders and monitoring officers and ktas and then this can be deleted
SET @applicant_role_id =
(SELECT id FROM role WHERE name = 'applicant');
SET @assessor_role_id =
(SELECT id FROM role WHERE name = 'assessor');
