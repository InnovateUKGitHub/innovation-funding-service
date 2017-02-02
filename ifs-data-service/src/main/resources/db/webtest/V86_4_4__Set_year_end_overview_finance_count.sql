-- This script is only temporary.
-- Once the competition templates have been migrated to and another baseline of web test data performed this script
-- should be removed.
--
-- Set the active flag for the form inputs of the type:
-- FINANCIAL_YEAR_END, FINANCIAL_OVERVIEW_ROW, FINANCIAL_STAFF_COUNT
-- based on:
-- active for sector competitions
-- inactive for programme competitions
-- inactive for the default competition - there were never any questions of this type.
SET @financial_year_end_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_YEAR_END');
SET @financial_overview_row_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_OVERVIEW_ROW');
SET @financial_staff_count_type_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_STAFF_COUNT');

-- Set the variables for programme and section template competition types
SET @programme_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE `name` = 'Programme');
SET @sector_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE `name` = 'Sector');

-- Currently the only competition of type sector is the template - set active to false for all of the relevant
-- form inputs then change to active for the sector template.
UPDATE form_input SET active = false
 WHERE form_input_type_id=@financial_year_end_type_id OR form_input_type_id=@financial_overview_row_type_id OR form_input_type_id=@financial_staff_count_type_id;
UPDATE form_input SET active = true
 WHERE competition_id=@sector_competition_template_id
   AND (form_input_type_id=@financial_year_end_type_id OR form_input_type_id=@financial_overview_row_type_id OR form_input_type_id=@financial_staff_count_type_id);
