-- Correct the values of section.display_in_assessment_application_summary for all competitions
UPDATE section
SET display_in_assessment_application_summary = 0
WHERE
name IN ('Your project costs', 'Your organisation', 'Your funding', 'Finances overview');