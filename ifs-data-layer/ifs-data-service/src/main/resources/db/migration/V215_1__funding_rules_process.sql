ALTER TABLE activity_state MODIFY COLUMN activity_type enum('APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS',
'PROJECT_SETUP_PROJECT_DETAILS','PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS',
'PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE','PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION',
'ASSESSMENT_REVIEW','ASSESSMENT_INTERVIEW_PANEL','ASSESSMENT_INTERVIEW', 'SUPPORTER_ASSIGNMENT', 'PROJECT_SETUP_PAYMENT_MILESTONES',
'PROJECT_SETUP_FUNDING_RULES');