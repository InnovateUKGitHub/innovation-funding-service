-- Example: add the new question section to Programme competition type (template competition id=2)
-- select * from question where competition_id=2;

SET @programme_template_id = (SELECT template_competition_id FROM competition_type where name="Programme");
SET @project_details_section_id = (SELECT id from section where competition_id=@programme_template_id and name="Project details");


-- Bump the priority of all the existing questions to to make space for 'Application team' at the top
UPDATE question SET priority = priority + 1 WHERE competition_id=@programme_template_id and question_type="GENERAL";

-- Add application team question with priority 1
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    VALUES (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @programme_template_id, @project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM');
SET @new_question_id = LAST_INSERT_ID();

-- The following form input stuff is hopefully temporary...
-- Ideally Questions of 'LEAD_ONLY' input type will not need form_inputs
-- and 'application details' question will also be changed to fit that model and its related form inputs removed

-- Add a new form input type for the application team page
INSERT INTO form_input_type (name)
    VALUES ('APPLICATION_TEAM');
SET @new_form_input_type_id = LAST_INSERT_ID();

-- Add a new form input attached to the Application team question, with the new form input type
INSERT INTO form_input (id, form_input_type_id, competition_id, included_in_application_summary, description, priority, question_id, scope, active)
    VALUES (NULL, @new_form_input_type_id, @programme_template_id, false, 'Application team', '0', @new_question_id, 'APPLICATION', true);



