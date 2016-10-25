# Set the flag display_in_assessment_application_summary to be true for sections of Competitions 2-6 that should be assessed

# Project details sections
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id in(16,27,38,49,60);

# Application questions sections
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id in(71,72,73,74,75);

# Finances
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id in(17,28,39,50,61);

# Finances overview
UPDATE `section` SET `display_in_assessment_application_summary`=1 WHERE id in(19,30,41,52,63);