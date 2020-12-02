SET @monitoring_officer_role_id =
(SELECT id FROM role WHERE name = 'monitoring_officer');

SET @stakeholder_role_id =
(SELECT id FROM role WHERE name = 'stakeholder');

SET @assessor_role_id =
(SELECT id FROM role WHERE name = 'assessor');

SET @kta_role_id =
(SELECT id FROM role WHERE name = 'knowledge_transfer_adviser');

SET @applicant_role_id =
(SELECT id FROM role WHERE name = 'applicant');

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'blake.wood@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'hubert.cumberdale@salad-fingers.com'), @applicant_role_id);