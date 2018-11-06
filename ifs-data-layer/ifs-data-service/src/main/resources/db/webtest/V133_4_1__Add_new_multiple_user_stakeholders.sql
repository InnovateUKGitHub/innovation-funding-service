-- IFS-4189: Add new stakeholders.

SET @ifs_web_system_user_id =
(SELECT id FROM user WHERE email = 'ifs_web_user@innovateuk.org');

SET @stakeholder_role_id =
(SELECT id FROM role WHERE name = 'stakeholder');

SET @assessor_role_id =
(SELECT id FROM role WHERE name = 'assessor');

SET @applicant_role_id =
(SELECT id FROM role WHERE name = 'applicant');

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'blake.wood@gmail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'gene.bowman@jetpulse.example.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'tera.maynard@example.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'tera.maynard@example.com'), @applicant_role_id);
