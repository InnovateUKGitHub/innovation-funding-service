-- IFS-4350 Indicate if the competition has a project growth table
ALTER TABLE competition ADD include_project_growth_table BIT(1) NULL AFTER application_finance_type;

SET @staff_count_form_input_type_id = (SELECT id FROM form_input_type WHERE name='FINANCIAL_STAFF_COUNT');

UPDATE competition c SET include_project_growth_table = (SELECT active FROM form_input fi WHERE competition_id = c.id
AND form_input_Type_id = @staff_count_form_input_type_id);