-- auditable fields
ALTER TABLE `user` ADD COLUMN created_by BIGINT(20) NULL;
ALTER TABLE `user` ADD COLUMN created_on DATETIME NULL;
ALTER TABLE `user` ADD COLUMN modified_by BIGINT(20) NULL;
ALTER TABLE `user` ADD COLUMN modified_on DATETIME NULL;

SET @sys_user = (SELECT u.id FROM `user` AS u JOIN user_role AS ur ON ur.user_id = u.id JOIN role AS r ON r.id = ur.role_id WHERE r.name = 'system_registrar' LIMIT 1);
UPDATE `user` SET created_by = @sys_user;
UPDATE `user` SET created_on = NOW();
UPDATE `user` SET modified_by = @sys_user;
UPDATE `user` SET modified_on = NOW();

ALTER TABLE `user` MODIFY COLUMN created_by BIGINT(20) NOT NULL;
ALTER TABLE `user` MODIFY COLUMN created_on DATETIME NOT NULL;
ALTER TABLE `user` MODIFY COLUMN modified_by BIGINT(20) NOT NULL;
ALTER TABLE `user` MODIFY COLUMN modified_on DATETIME NOT NULL;

ALTER TABLE `user` ADD CONSTRAINT user_created_by_to_user_fk FOREIGN KEY (created_by) REFERENCES `user` (id);
ALTER TABLE `user` ADD CONSTRAINT user_modified_by_to_user_fk FOREIGN KEY (modified_by) REFERENCES `user` (id);
