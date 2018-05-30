-- Example: add the new question section to Programme competition type (template competition id=2)
-- select * from question where competition_id=2;

SET @programme_template_id = (SELECT template_competition_id FROM competition_type where name="Programme");
SET @project_details_section_id = (SELECT id from section where competition_id=@programme_template_id and name="Project details");


-- Bump the priority of all the existing questions to to make space for 'Application team' at the top
UPDATE question SET priority = priority + 1 WHERE competition_id=@programme_template_id and question_type="GENERAL";

-- Add application team question with priority 1
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    VALUES (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @programme_template_id, @project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM');



