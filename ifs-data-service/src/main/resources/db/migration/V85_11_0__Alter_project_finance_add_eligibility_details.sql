ALTER TABLE project_finance ADD COLUMN `eligibility` ENUM('REVIEW', 'APPROVED') NOT NULL DEFAULT 'REVIEW';

ALTER TABLE project_finance ADD COLUMN `eligibility_status` ENUM('UNSET', 'GREEN', 'AMBER', 'RED') NOT NULL DEFAULT 'UNSET';

ALTER TABLE project_finance ADD COLUMN `eligibility_approval_user_id` BIGINT(20) NULL;
ALTER TABLE project_finance ADD COLUMN `eligibility_approval_date` DATE NULL;

ALTER TABLE project_finance ADD CONSTRAINT FK_project_finance_to_user2 FOREIGN KEY (eligibility_approval_user_id) REFERENCES user(id);