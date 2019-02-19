SET @monitoring_officer_role_id =
(SELECT id FROM role WHERE name = 'monitoring_officer');

SET @stakeholder_role_id =
(SELECT id FROM role WHERE name = 'stakeholder');

SET @assessor_role_id =
(SELECT id FROM role WHERE name = 'assessor');

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'orville.Gibbs@gmail.com'), @monitoring_officer_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Nilesh.Patti@gmail.com'), @monitoring_officer_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Rupesh.Pereira@gmail.com'), @monitoring_officer_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Rayon.Kevin@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Rui.Lemos@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Lance.Lemos@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Kingsley.Roy@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Sonal.Dsilva@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'carolyn.reed@example.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'carolyn.reed@example.com'), @assessor_role_id);