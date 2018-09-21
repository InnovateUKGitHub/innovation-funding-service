-- IFS-3977: Add new monitoring offiers.

SET @ifs_web_system_user_id =
(SELECT id FROM user WHERE email = 'ifs_web_user@innovateuk.org');

SET @monitoring_officer_role_id =
(SELECT id FROM role WHERE name = 'monitoring_officer');

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Orville.Gibbs@gmail.com', 'Orville', 'Gibbs', '449823090017', 'ACTIVE', '623d49e5-816e-4acf-b95a-5d712562e037', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Nilesh.Patti@gmail.com', 'Nilesh', 'Patti', '449890325459', 'ACTIVE', 'ea4bc32f-4149-444b-a781-02c6b08ae0d1', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO `user`
(email, first_name, last_name, phone_number, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on)
VALUES ('Rupesh.Pereira@gmail.com', 'Rupesh', 'Pereira', '448554937227', 'ACTIVE', '4486a84a-fbef-419a-9cd0-032170031c97', 0, 0, @ifs_web_system_user_id, NOW(), @ifs_web_system_user_id, NOW());

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Orville.Gibbs@gmail.com'), @monitoring_officer_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Nilesh.Patti@gmail.com'), @monitoring_officer_role_id);

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'Rupesh.Pereira@gmail.com'), @monitoring_officer_role_id);


