-- IFS-2123: add a new question for 'Research category' to the competition templates. All subsequent competitions
--  that are created will include this new question.

-- Bump the priority of all the existing questions to to make space for the 'Research category' question at the top
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

-- Add research category question with priority 1 for each of the template competitions
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    VALUES
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3',
    @programme_template_id, @programme_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @sector_template_id,
    @sector_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @generic_template_id,
     @generic_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @eoi_template_id,
    @eoi_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @apc_template_id,
    @apc_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @ati_template_id,
    @ati_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY'),
    (false, 'Description not used', true, false, 'Research category', 'Research category', '3', @princes_template_id,
     @princes_project_details_section_id, 'LEAD_ONLY', 'RESEARCH_CATEGORY');