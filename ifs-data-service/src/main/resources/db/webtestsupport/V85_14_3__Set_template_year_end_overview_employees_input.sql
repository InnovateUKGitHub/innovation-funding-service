-- Set the default presence (i.e. 'active'-ness) field for the year end, overview and financial staff count form inputs on the template competitions
-- Set these form inputs to be inactive for the programme template (and active for the sector template)
-- Hence these inputs will be inactive by default for all programme competitions and active by default for all sector competitions made from these templates.

-- Set the variables for the form_input types to change
SET @financial_year_end_type_id = (SELECT id from form_input_type where name =  'FINANCIAL_YEAR_END');
SET @financial_overview_row_type_id = (SELECT id from form_input_type where name =  'FINANCIAL_OVERVIEW_ROW');
SET @financial_staff_count_type_id = (SELECT id from form_input_type where name =  'FINANCIAL_STAFF_COUNT');


-- Set the variables for programme and section template competition types
SET @programme_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Programme');
SET @sector_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Sector');

-- Update the active state of the form inputs.
UPDATE `form_input` SET `active`=0
    WHERE competition_id=@programme_competition_template_id AND (form_input_type_id=@financial_year_end_type_id OR form_input_type_id=@financial_overview_row_type_id OR form_input_type_id=@financial_staff_count_type_id);
UPDATE `form_input` SET `active`=1
    WHERE competition_id=@sector_competition_template_id AND (form_input_type_id=@financial_year_end_type_id OR form_input_type_id=@financial_overview_row_type_id OR form_input_type_id=@financial_staff_count_type_id);
