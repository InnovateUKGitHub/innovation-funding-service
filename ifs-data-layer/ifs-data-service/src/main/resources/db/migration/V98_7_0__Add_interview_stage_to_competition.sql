-- Add boolean interview_stage column to competition table
ALTER TABLE `competition`
ADD COLUMN `add_interview_stage` BIT(1) DEFAULT null AFTER `use_assessment_panel`;

UPDATE `competition` SET `add_interview_stage`=b'0'
