-- IFS-2335 - EOI competitions should have a nulled `full_application_finance` column to represent having 'No Finances'.

UPDATE competition SET full_application_finance = null WHERE name = 'Template for the Expression of interest competition type';
