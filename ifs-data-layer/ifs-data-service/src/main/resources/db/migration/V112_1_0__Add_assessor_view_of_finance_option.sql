-- Add boolean assessor_view column to competition table
ALTER TABLE `competition`
ADD COLUMN `finance_view` ENUM('OVERVIEW', 'DETAILED') NOT NULL DEFAULT 'OVERVIEW' AFTER `has_interview_stage`;
