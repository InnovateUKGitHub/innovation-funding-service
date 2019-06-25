-- IFS-5758

-- Add descriminator value for project_user
ALTER TABLE project_user MODIFY type ENUM('PROJECT_USER', 'PROJECT_MONITORING_OFFICER', 'FINANCE_REVIEWER') NOT NULL;

-- add new role to enum table.s
INSERT INTO project_role (name) VALUES ('FINANCE_REVIEWER');
