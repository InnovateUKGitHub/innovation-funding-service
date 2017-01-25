-- TODO after baselining the data set remove the corresponding script from webtest
-- Add a form input under the organisation size question for all competitions and templates. Default is inactive.
-- Financial Year End. Get the form input type and then do an insert for every organisation size question.
SET @financial_year_end_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_YEAR_END');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_year_end_id, q.competition_id, false, "End of last financial year", "Enter the month and year that your last financial year finished.", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';

-- Financial Overview rows.
SET @financial_overview_row_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_OVERVIEW_ROW');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual Turnover", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual Profit", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Annual Exports", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_overview_row_id, q.competition_id, false, "Research and development spend", "", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';

-- Full time employees at year end
SET @financial_staff_count_id = (SELECT id FROM form_input_type WHERE `name` =  'FINANCIAL_STAFF_COUNT');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @financial_staff_count_id, q.competition_id, false, "Full time employees", "Number of full time employees at your organisation at the end of the last financial year.", "", (SELECT MAX(priority) + 1 FROM form_input AS fi WHERE q.id = fi.question_id), q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';

-- Connect the validator to the form inputs
SET @past_month_validator_id = (SELECT id FROM form_validator WHERE title = 'PastMMYYYYValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @past_month_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_year_end_id);

SET @integer_validator_id = (SELECT id FROM form_validator WHERE title = 'IntegerValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @integer_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_overview_row_id);

-- Connect the validator to the form inputs
SET @non_negative_integer_validator_id = (SELECT id FROM form_validator WHERE title = 'NonNegativeIntegerValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @non_negative_integer_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@financial_staff_count_id);







