ALTER TABLE project_finance DROP FOREIGN KEY FK_project_finance_to_user;
ALTER TABLE project_finance DROP FOREIGN KEY FK_project_finance_to_user2;

ALTER TABLE project_finance DROP COLUMN viability;
ALTER TABLE project_finance DROP COLUMN viability_approval_user_id;
ALTER TABLE project_finance DROP COLUMN viability_approval_date;

ALTER TABLE project_finance DROP COLUMN eligibility;
ALTER TABLE project_finance DROP COLUMN eligibility_approval_user_id;
ALTER TABLE project_finance DROP COLUMN eligibility_approval_date;