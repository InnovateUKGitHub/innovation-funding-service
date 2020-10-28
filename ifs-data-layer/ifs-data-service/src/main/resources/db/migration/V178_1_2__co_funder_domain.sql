
ALTER TABLE activity_state MODIFY COLUMN activity_type enum('APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS',
'PROJECT_SETUP_PROJECT_DETAILS','PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS',
'PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE','PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION',
'ASSESSMENT_REVIEW','ASSESSMENT_INTERVIEW_PANEL','ASSESSMENT_INTERVIEW', 'COFUNDER_ASSIGNMENT', 'SUPPORTER_ASSIGNMENT');

UPDATE activity_state SET activity_type = 'SUPPORTER_ASSIGNMENT' WHERE activity_type = 'COFUNDER_ASSIGNMENT';

ALTER TABLE activity_state MODIFY COLUMN activity_type enum('APPLICATION_ASSESSMENT','PROJECT_SETUP','PROJECT_SETUP_COMPANIES_HOUSE_DETAILS',
'PROJECT_SETUP_PROJECT_DETAILS','PROJECT_SETUP_MONITORING_OFFICER_ASSIGNMENT','PROJECT_SETUP_BANK_DETAILS','PROJECT_SETUP_FINANCE_CHECKS',
'PROJECT_SETUP_VIABILITY','PROJECT_SETUP_ELIGIBILITY','PROJECT_SETUP_SPEND_PROFILE','PROJECT_SETUP_GRANT_OFFER_LETTER','APPLICATION',
'ASSESSMENT_REVIEW','ASSESSMENT_INTERVIEW_PANEL','ASSESSMENT_INTERVIEW', 'SUPPORTER_ASSIGNMENT');

UPDATE role SET name = 'supporter' WHERE name = 'cofunder';