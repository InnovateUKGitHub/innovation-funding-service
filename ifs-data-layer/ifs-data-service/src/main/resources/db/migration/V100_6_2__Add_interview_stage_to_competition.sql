-- Add boolean interview_stage column to competition table
ALTER TABLE `competition`
ADD COLUMN `has_interview_stage` BIT(1) DEFAULT null AFTER `has_assessment_panel`;

UPDATE `competition` SET `has_interview_stage`=b'0'
