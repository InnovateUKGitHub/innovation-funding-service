SET @support_role_id =
(SELECT id FROM role WHERE name = 'support');

INSERT INTO user_role
(user_id, role_id)
VALUES
((select id from user where email = 'orville.Gibbs@gmail.com'), @support_role_id);
