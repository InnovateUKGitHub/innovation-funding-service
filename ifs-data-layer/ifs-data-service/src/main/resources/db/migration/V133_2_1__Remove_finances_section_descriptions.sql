-- IFS-2681 Remove finance sections descriptions from the Competition data model. They will be determined in code now.
UPDATE section set description = NULL WHERE section_type = 'GENERAL' and name = 'Finances';
UPDATE section set description = NULL WHERE section_type = 'OVERVIEW_FINANCES';