SET @existingApplicationId =1;
SET @newApplicationId =7;

-- Insert application (same as application.id 1)
INSERT INTO application(id, duration_in_months, name, start_date, competition, completion)
SELECT @newApplicationId, duration_in_months, name, start_date, competition, completion
FROM application
where id = @existingApplicationId;

-- Insert add application begin state (CREATED)
INSERT INTO process(last_modified, process_type, target_id, activity_state_id, version)
SELECT last_modified, process_type, @newApplicationId, activity_state_id, version
FROM process
where target_id = @existingApplicationId
and process_type = "ApplicationProcess";

-- Insert process_roles (same as application.id 1)
INSERT INTO process_role(application_id, organisation_id, role_id, user_id)
SELECT @newApplicationId, organisation_id, role_id, user_id
FROM process_role
where application_id = @existingApplicationId;

-- Insert question statuses to this application
INSERT INTO question_status(assigned_date, marked_as_complete, application_id, marked_as_complete_by_id, question_id)
SELECT NOW(), 1, @newApplicationId, pr.id, q.id
FROM process_role pr LEFT JOIN question q on q.competition_id = 1
WHERE pr.application_id =@newApplicationId;

-- Insert form input responses to this application
INSERT INTO form_input_response(update_date, value, form_input_id, updated_by_id, application_id)
SELECT NOW(), "1", fi.id, pr.id, @newApplicationId
FROM process_role pr LEFT JOIN form_input fi on fi.competition_id = 1
WHERE pr.application_id =@newApplicationId;

