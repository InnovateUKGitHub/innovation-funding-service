-- IFS-4616 New field to indicate if the Your organisation page for Research applications is included
ALTER TABLE competition ADD include_your_organisation_section BOOLEAN NULL AFTER include_project_growth_table;

-- For all existing competitions the option to 'Include 'Your organisation' section for research organisations' is set
--  to 'No' to align with existing functionality.
UPDATE competition SET include_your_organisation_section = FALSE WHERE include_your_organisation_section IS NULL;