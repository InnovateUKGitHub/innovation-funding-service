-- Set the default presence (i.e. 'active'-ness) field for the turnover and staff count form inputs on the template competitions
-- Set both these form inputs to be active for the programme template (and inactive for the sector template)
-- Hence these inputs will be active by default for all programme competitions and inactive by default for all sector competitions made from these templates.

-- Set the variables for the form_input types to change
SET @staff_turnover_type_id = (SELECT id from form_input_type where name =  'STAFF_TURNOVER');
SET @staff_count_type_id = (SELECT id from form_input_type where name =  'STAFF_COUNT');

-- Set the variables for programme and section template competition types
SET @programme_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Programme');
SET @sector_competition_template_id=(SELECT template_competition_id FROM competition_type WHERE name='Sector');

-- Update the active state of the form inputs.
UPDATE `form_input` SET `active`=1
    WHERE competition_id=@programme_competition_template_id AND (form_input_type_id=@staff_turnover_type_id OR form_input_type_id=@staff_count_type_id);
UPDATE `form_input` SET `active`=0
    WHERE competition_id=@sector_competition_template_id AND (form_input_type_id=@staff_turnover_type_id OR form_input_type_id=@staff_count_type_id);
