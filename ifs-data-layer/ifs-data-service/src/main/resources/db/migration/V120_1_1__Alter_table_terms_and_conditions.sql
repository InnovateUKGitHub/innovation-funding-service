--IFS-3093

SET @system_maintenance_user_id = (SELECT id
                                   FROM user
                                   WHERE email = 'ifs_system_maintenance_user@innovateuk.org');

-- Add type to terms_and_conditions and make it auditable
ALTER TABLE terms_and_conditions
  ADD COLUMN `type` enum('SITE','GRANT') NOT NULL,
  ADD COLUMN `created_by` bigint(20) NOT NULL,
  ADD COLUMN `created_on` datetime NOT NULL,
  ADD COLUMN `modified_on` datetime NOT NULL,
  ADD COLUMN `modified_by` bigint(20) NOT NULL;

-- Change the type of the version column from varchar to int
ALTER TABLE terms_and_conditions MODIFY `version` smallint(20) NOT NULL;

-- Set all existing rows to be type 'GRANT'
-- Set all existing rows with now() as the created/modified dates
-- Set all existing rows with the system maintenance user id as the created/modified by
UPDATE terms_and_conditions
SET `type`      = 'GRANT',
  `created_by`  = @system_maintenance_user_id,
  `created_on`  = NOW(),
  `modified_by` = @system_maintenance_user_id,
  `modified_on` = NOW();

ALTER TABLE terms_and_conditions
  ADD CONSTRAINT `terms_and_conditions_created_by_to_user_fk` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `terms_and_conditions_modified_by_to_user_fk` FOREIGN KEY (`modified_by`) REFERENCES `user` (`id`);