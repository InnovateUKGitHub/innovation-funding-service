-- IFS-4189: Add new stakeholders.

SET @ifs_web_system_user_id =
(SELECT id FROM user WHERE email = 'ifs_web_user@innovateuk.org');

SET @stakeholder_role_id =
(SELECT id FROM role WHERE name = 'stakeholder');

SET @assessor_role_id =
(SELECT id FROM role WHERE name = 'assessor');

SET @applicant_role_id =
(SELECT id FROM role WHERE name = 'applicant');

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Bob.Lo@mail.com', 'Bobby', 'Lopez', '447951237865', 'ACTIVE', 'a47f7efe-e820-4bab-823b-d6827e8bd2e2', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Mel.Fanning@mail.com', 'Melisa', 'Fanning', '447945789023', 'ACTIVE', '86fef93d-4392-4bd5-9f29-1471952dba0b', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Seth.Green@mail.com', 'Seth', 'Green', '447234789267', 'ACTIVE', '71e52a51-4294-44d4-b709-2c505f071a72', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Bob.Lo@mail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Bob.Lo@mail.com'), @assessor_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Mel.Fanning@mail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Mel.Fanning@mail.com'), @applicant_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Seth.Green@mail.com'), @stakeholder_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Seth.Green@mail.com'), @assessor_role_id);
INSERT INTO user_role
(user_id, role_id)
VALUES
((SELECT id FROM user WHERE email = 'Seth.Green@mail.com'), @applicant_role_id);
