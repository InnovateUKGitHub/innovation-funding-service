-- Add a form input under the organisation size question for all competitions and templates. Default is inactive.
-- Staff turnover. Get the form input type and then do an insert for every organisation size question. Priority is 1 as there is already exists a form input.
SET @staff_turnover_type_id = (SELECT id from form_input_type where name =  'STAFF_TURNOVER');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @staff_turnover_type_id, q.competition_id, false, "Turnover (£)", "Your turnover from the last financial year.", "", 1, q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';
-- Staff count. Get the form input type and then do an insert for every organisation size question. Priority is 2 as there are now two form inputs.
SET @staff_count_type_id = (SELECT id from form_input_type where name =  'STAFF_COUNT');
INSERT INTO form_input (word_count, form_input_type_id, competition_id, included_in_application_summary, description, guidance_title, guidance_answer, priority, question_id, scope, active)
     SELECT null, @staff_count_type_id, q.competition_id, false, "Full time employees", "Number of full time employees at your organisation.", "", 2, q.id AS `question_id`, "APPLICATION", false
     FROM question AS q
     WHERE q.name = 'Organisation Size';

-- Connect the validator to the form inputs
SET @non_negative_integer_validator_id = (SELECT id FROM form_validator WHERE title = 'NonNegativeIntegerValidator');
INSERT INTO form_input_validator (`form_input_id`, `form_validator_id`)
     SELECT fi.id, @non_negative_integer_validator_id
     FROM form_input AS fi
    WHERE fi.form_input_type_id IN (@staff_turnover_type_id, @staff_count_type_id);


