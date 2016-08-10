-- Remove duplicate user_role entries
DELETE FROM user_role WHERE user_id = 1 AND role_id = 4;
DELETE FROM user_role WHERE user_id = 2 AND role_id = 4;
DELETE FROM user_role WHERE user_id = 3 AND role_id = 3;
DELETE FROM user_role WHERE user_id = 8 AND role_id = 4;
INSERT INTO user_role (user_id, role_id) VALUES (1, 4);
INSERT INTO user_role (user_id, role_id) VALUES (2, 4);
INSERT INTO user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO user_role (user_id, role_id) VALUES (8, 4);