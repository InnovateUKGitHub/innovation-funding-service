/*
-- TODO we're not going to need this flag
ALTER TABLE application ADD COLUMN in_assessment_interview_panel BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX application_in_assessment_interview_panel_idx ON application(in_assessment_interview_panel);
*/

-- rename existing in_assessment_panel flag
ALTER TABLE application CHANGE in_assessment_panel in_assessment_review_panel BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE application
  DROP INDEX application_in_assessment_panel,
  ADD INDEX application_in_assessment_review_panel_idx (in_assessment_review_panel);

-- TODO rename this file

ALTER TABLE activity_state
MODIFY COLUMN
  activity_type enum(
    'APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS','PROJECT_SETUP_PROJECT_DETAILS',
    'PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS',
    'PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE',
    'PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION','ASSESSMENT_PANEL_APPLICATION_INVITE',
    'ASSESSMENT_INTERVIEW_PANEL'
) NOT NULL;

INSERT INTO activity_state (activity_type, state) VALUES
('ASSESSMENT_INTERVIEW_PANEL', 'CREATED'),
('ASSESSMENT_INTERVIEW_PANEL', 'PENDING'),
('ASSESSMENT_INTERVIEW_PANEL', 'SUBMITTED');
