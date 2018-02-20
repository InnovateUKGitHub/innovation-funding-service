-- Add boolean assessor_view column to competition table
ALTER TABLE `competition`
ADD COLUMN `has_full_finance_view` BIT(1) DEFAULT null AFTER `has_interview_stage`;

UPDATE `competition` SET `has_full_finance_view`=b'0'