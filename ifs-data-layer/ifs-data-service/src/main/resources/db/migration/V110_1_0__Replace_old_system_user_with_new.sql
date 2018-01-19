/**
  This is contract phase of IFS-2419.  A new system registration user was inserted for IFS-2419 with new UUID.
  Old one wasn't removed to allow for ZDD.  This user can now be removed on next deploy.  This script achieves this
  by removing old user and updating email address of new user.
 */

SELECT id INTO @old_sys_web_user FROM user WHERE email='ifs_web_user@innovateuk.org';
SELECT id INTO @new_sys_web_user FROM user WHERE email='ifs_web_user_1@innovateuk.org';

UPDATE user SET created_by = @new_sys_web_user WHERE created_by = @old_sys_web_user;
UPDATE user SET modified_by = @new_sys_web_user WHERE modified_by = @old_sys_web_user;

UPDATE profile SET created_by = @new_sys_web_user WHERE created_by = @old_sys_web_user;
UPDATE profile SET modified_by = @new_sys_web_user WHERE modified_by = @old_sys_web_user;

DELETE FROM user_role WHERE user_id = @old_sys_web_user;

DELETE FROM user WHERE id = @old_sys_web_user;

UPDATE user SET email = 'ifs_web_user@innovateuk.org' WHERE email = 'ifs_web_user_1@innovateuk.org';

