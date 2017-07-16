-- Add boolean assessment_panel column to competition table
ALTER TABLE `competition`
ADD COLUMN `use_assessment_panel` BIT(1) DEFAULT null AFTER `assessor_pay`;

UPDATE `competition` SET `use_assessment_panel`=b'0'
