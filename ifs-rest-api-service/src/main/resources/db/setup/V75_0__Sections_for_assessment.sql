# Set the flag display_in_assessment_application_summary to be true for sections of Competition 1 that should be assessed

# Project details section
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id=1;

# Application questions section
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id=2;

# Finance
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id=6;

# Finances overview
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id=8;