-- IFS-2681

-- Remove finance section descriptions from the data model. They will be determined in code now.
UPDATE section set description = NULL WHERE section_type = 'GENERAL' and name = 'Finances';
UPDATE section set description = NULL WHERE section_type = 'OVERVIEW_FINANCES';

-- Remove question description from the data model. This will be determined in code now.
UPDATE question set description = NULL WHERE short_name = 'Project finances';