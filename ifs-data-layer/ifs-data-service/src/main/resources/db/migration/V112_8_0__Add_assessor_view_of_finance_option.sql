-- Add assessor_finance_view column to competition table
ALTER TABLE `competition`
ADD COLUMN `assessor_finance_view` ENUM('OVERVIEW', 'DETAILED') NOT NULL DEFAULT 'OVERVIEW' AFTER `has_interview_stage`;
