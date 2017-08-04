-- auditable fields
ALTER TABLE `user` ADD COLUMN created_by BIGINT(20) NULL;
ALTER TABLE `user` ADD COLUMN created_on DATETIME NULL;
ALTER TABLE `user` ADD COLUMN modified_by BIGINT(20) NULL;
ALTER TABLE `user` ADD COLUMN modified_on DATETIME NULL;

SET @sys_user = (SELECT id FROM `user` WHERE email = 'ifs_system_maintenance_user@innovateuk.org');
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
