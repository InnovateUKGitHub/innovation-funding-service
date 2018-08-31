-- IFS-4164 Make Competition auditable
SET @system_maintenance_user_id = (SELECT id
                                   FROM user
                                   WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

ALTER TABLE competition
  ADD COLUMN `created_by` bigint(20) NOT NULL,
  ADD COLUMN `created_on` datetime NOT NULL,
  ADD COLUMN `modified_on` datetime NOT NULL,
  ADD COLUMN `modified_by` bigint(20) NOT NULL;

UPDATE competition
SET `created_by` = @system_maintenance_user_id,
  `created_on`   = '1970-01-01 00:00:01',
  `modified_by`  = @system_maintenance_user_id,
  `modified_on`  = '1970-01-01 00:00:01';