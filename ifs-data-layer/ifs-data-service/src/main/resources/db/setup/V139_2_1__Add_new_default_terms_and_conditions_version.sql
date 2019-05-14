-- IFS-5335: Add new version of default Innovate UK terms and conditions

SET @system_maintenance_user_id = (SELECT `id` FROM `user` WHERE `email` = 'ifs_system_maintenance_user@innovateuk.org');

-- Add entry into terms and conditions table for version 3 of default Innovate UK terms and conditions
INSERT INTO `terms_and_conditions` (`name`, `template`, `version`, `type`, `created_by`, `created_on`, `modified_on`, `modified_by`)
VALUES
('Innovate UK','default-terms-and-conditions-v3', 3, 'GRANT', @system_maintenance_user_id, NOW(), NOW(), @system_maintenance_user_id);
SET @default_terms_and_conditions_id = (SELECT LAST_INSERT_ID());

-- Update link from template competition to new version of terms and conditions so these become the default Ts&Cs
--  used for new competitions of Sector, Programme, Generic, EOI and Horizon 2020 type.
UPDATE `competition` SET `terms_and_conditions_id` = @default_terms_and_conditions_id WHERE `id` IN
(SELECT `template_competition_id` FROM `competition_type` WHERE `name` IN
('Sector',
'Programme',
'Generic',
'Expression of interest',
'Horizon 2020'));

