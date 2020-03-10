-- IFS-7227 add acc projects roles

ALTER TABLE project_user MODIFY type ENUM('PROJECT_USER',
 'PROJECT_MONITORING_OFFICER',
 'FINANCE_REVIEWER',
 'ACC_FINANCE_REVIEWER',
 'ACC_PROJECT_MANAGER',
 'ACC_MONITORING_OFFICER') NOT NULL;

INSERT INTO project_role (name) VALUES ('ACC_PROJECT_PARTNER');
INSERT INTO project_role (name) VALUES ('ACC_PROJECT_MANAGER');
INSERT INTO project_role (name) VALUES ('ACC_PROJECT_FINANCE_CONTACT');
INSERT INTO project_role (name) VALUES ('ACC_MONITORING_OFFICER');

ALTER TABLE invite
    MODIFY COLUMN type enum('ROLE',
                            'COMPETITION',
                            'COMPETITION_STAKEHOLDER',
                            'COMPETITION_INNOVATION_LEAD',
                            'ASSESSMENT_PANEL',
                            'INTERVIEW_PANEL',
                            'PROJECT',
                            'PROJECT_PARTNER',
                            'MONITORING_OFFICER',
                            'APPLICATION',
                            'ACC_MONITORING_OFFICER',
                            'ACC_PROJECT_PARTNER',
                            'ACC_PROJECT_MANAGER',
                            'ACC_PROJECT_FINANCE_CONTACT');

-- INSERT INTO role (id, name, url) VALUES (22, 'acc_partner', 'applicant/dashboard');
-- INSERT INTO role (id, name, url) VALUES (23, 'acc_project_manager', 'applicant/dashboard');
-- INSERT INTO role (id, name, url) VALUES (24, 'acc_finance_contact', 'applicant/dashboard');
-- INSERT INTO role (id, name, url) VALUES (25, 'acc_monitoring_officer', 'applicant/dashboard');