-- TODO Configure user builder to register stakeholders and monitoring officers and then this can be deleted
SET @applicant_role_id =
(SELECT id FROM role WHERE name = 'applicant');

DELETE FROM user_role WHERE user_id = (select id from user where email = 'orville.Gibbs@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (select id from user where email = 'Nilesh.Patti@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (select id from user where email = 'Rupesh.Pereira@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (select id from user where email = 'Rayon.Kevin@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (SELECT id FROM user WHERE email = 'Rui.Lemos@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (SELECT id FROM user WHERE email = 'blake.wood@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (SELECT id FROM user WHERE email = 'Lance.Lemos@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (SELECT id FROM user WHERE email = 'Kingsley.Roy@gmail.com') AND role_id = @applicant_role_id;
DELETE FROM user_role WHERE user_id = (SELECT id FROM user WHERE email = 'Sonal.Dsilva@gmail.com') AND role_id = @applicant_role_id;