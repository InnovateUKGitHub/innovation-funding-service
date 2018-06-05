-- IFS-3088: add a new question for 'Application Team' to the competition templates. All subsequent competitions
--  that are created will include this new question.
-- Also set a new question type LEAD_ONLY for this question and for 'Application Details' to mark them as pages that
--  do not display using form_inputs like standard questions.

-- Bump the priority of all the existing questions to to make space for the 'Application team' question at the top
UPDATE question SET priority = priority + 1 WHERE question_type="GENERAL" AND competition_id IN (
    SELECT template_competition_id FROM competition_type
);

-- Find the competition ids for all the competition type templates
SET @programme_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Programme");
SET @sector_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Sector");
SET @generic_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Generic");
SET @eoi_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Expression of interest");
SET @apc_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Advanced Propulsion Centre");
SET @ati_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Aerospace Technology Institute");
SET @princes_template_id = (SELECT template_competition_id FROM competition_type WHERE name="The Prince's Trust");

-- Find the section ids for the project details section under each competition
SET @programme_project_details_section_id = (SELECT id FROM section WHERE competition_id=@programme_template_id AND name="Project details");
SET @sector_project_details_section_id = (SELECT id FROM section WHERE competition_id=@sector_template_id AND name="Project details");
SET @generic_project_details_section_id = (SELECT id FROM section WHERE competition_id=@generic_template_id AND name="Project details");
SET @eoi_project_details_section_id = (SELECT id FROM section WHERE competition_id=@eoi_template_id AND name="Project details");
SET @apc_project_details_section_id = (SELECT id FROM section WHERE competition_id=@apc_template_id AND name="Project details");
SET @ati_project_details_section_id = (SELECT id FROM section WHERE competition_id=@ati_template_id AND name="Project details");
SET @princes_project_details_section_id = (SELECT id FROM section WHERE competition_id=@princes_template_id AND name="Project details");

-- Add application team question with priority 1 for each of the template competitions
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    VALUES
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @programme_template_id, @programme_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @sector_template_id, @sector_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @generic_template_id, @generic_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @eoi_template_id, @eoi_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @apc_template_id, @apc_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @ati_template_id, @ati_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM'),
    (false, 'Description not used', true, false, 'Application team', 'Application team', '1', @princes_template_id, @princes_project_details_section_id, 'LEAD_ONLY', 'APPLICATION_TEAM');

-- Update all Application details questions, so that they have type LEAD_ONLY
UPDATE question SET question_type='LEAD_ONLY' WHERE question_setup_type='APPLICATION_DETAILS';