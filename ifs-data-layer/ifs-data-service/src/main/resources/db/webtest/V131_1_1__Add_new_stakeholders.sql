-- IFS-4189: Add new stakeholders.

SET @ifs_web_system_user_id =
(SELECT id FROM user WHERE email = 'ifs_web_user@innovateuk.org');

SET @stakeholder_role_id =
(SELECT id FROM role WHERE name = 'stakeholder');

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Rui.Lemos@gmail.com', 'Rui', 'Lemos', '449823463581', 'ACTIVE', 'a22a995b-e8bf-47af-b0eb-efe01dc94d6e', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Lance.Lemos@gmail.com', 'Lance', 'Lemos', '449823463582', 'ACTIVE', '18f825f3-1466-4792-8c15-f87c3c2a5536', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Kingsley.Roy@gmail.com', 'Kingsley', 'Roy', '449823463583', 'ACTIVE', 'a16fd6c1-aad8-4b84-a102-bc8a65d6df29', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Sonal.Dsilva@gmail.com', 'Sonal', 'Dsilva', '449823463584', 'ACTIVE', 'c64bc71b-0f6f-4b7e-b64d-b96d0838590d', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

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




