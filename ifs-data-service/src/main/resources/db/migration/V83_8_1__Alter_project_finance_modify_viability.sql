--Add REVIEW and NOT_APPLICABLE enums
ALTER TABLE project_finance MODIFY COLUMN `viability` ENUM('PENDING', 'REVIEW', 'APPROVED', 'NOT_APPLICABLE') NOT NULL DEFAULT 'REVIEW';

--Update existing records which are PENDING to REVIEW
UPDATE project_finance
SET viability = 'REVIEW'
WHERE viability = 'PENDING';

--Drop the PENDING enum
ALTER TABLE project_finance MODIFY COLUMN `viability` ENUM('REVIEW', 'APPROVED', 'NOT_APPLICABLE') NOT NULL DEFAULT 'REVIEW';

