-- IFS-7227 add grants projects roles

INSERT INTO project_role (name) VALUES ('GRANTS_PROJECT_MANAGER');
INSERT INTO project_role (name) VALUES ('GRANTS_PROJECT_FINANCE_CONTACT');
INSERT INTO project_role (name) VALUES ('GRANTS_MONITORING_OFFICER');

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
                            'GRANTS_MONITORING_OFFICER',
                            'GRANTS_PROJECT_MANAGER',
                            'GRANTS_PROJECT_FINANCE_CONTACT');