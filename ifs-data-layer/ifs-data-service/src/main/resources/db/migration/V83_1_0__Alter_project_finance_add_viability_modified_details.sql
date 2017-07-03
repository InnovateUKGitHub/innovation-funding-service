ALTER TABLE project_finance ADD COLUMN `viability_approval_user_id` BIGINT(20) NULL;
ALTER TABLE project_finance ADD COLUMN `viability_approval_date` DATE NULL;

ALTER TABLE project_finance ADD CONSTRAINT FK_project_finance_to_user FOREIGN KEY (viability_approval_user_id) REFERENCES user(id);