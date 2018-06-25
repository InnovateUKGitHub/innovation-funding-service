-- Bump the priority of all the existing sections to to make space for the 'Project location' section at the top
UPDATE section SET priority = priority + 1 WHERE section_type in ("ORGANISATION_FINANCES", "FUNDING_FINANCES", "OVERVIEW_FINANCES") AND competition_id IN (
    SELECT template_competition_id FROM competition_type
);

-- Find the competition ids for all the competition type templates
SET @programme_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Programme");
SET @sector_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Sector");
SET @generic_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Generic");
SET @apc_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Advanced Propulsion Centre");
SET @ati_template_id = (SELECT template_competition_id FROM competition_type WHERE name="Aerospace Technology Institute");

-- Find the section ids for the your finances section under each competition
SET @programme_your_funding_section_id = (SELECT id FROM section WHERE competition_id=@programme_template_id AND section_type="FINANCE");
SET @sector_your_funding_section_id = (SELECT id FROM section WHERE competition_id=@sector_template_id AND section_type="FINANCE");
SET @generic_your_funding_section_id = (SELECT id FROM section WHERE competition_id=@generic_template_id AND section_type="FINANCE");
SET @apc_your_funding_section_id = (SELECT id FROM section WHERE competition_id=@apc_template_id AND section_type="FINANCE");
SET @ati_your_funding_section_id = (SELECT id FROM section WHERE competition_id=@ati_template_id AND section_type="FINANCE");

-- Add 'Your project location' section with priority 6 for each of the template competitions
INSERT INTO section (assessor_guidance_description, description, display_in_assessment_application_summary, name, priority, competition_id, parent_section_id, question_group, section_type)
     VALUES
     (NULL, 'Where will most of the project work take place?', 0, 'Your project location', 6, @programme_template_id, @programme_your_funding_section_id, 1, "PROJECT_LOCATION"),
     (NULL, 'Where will most of the project work take place?', 0, 'Your project location', 6, @sector_template_id, @sector_your_funding_section_id, 1, "PROJECT_LOCATION"),
     (NULL, 'Where will most of the project work take place?', 0, 'Your project location', 6, @generic_template_id, @generic_your_funding_section_id, 1, "PROJECT_LOCATION"),
     (NULL, 'Where will most of the project work take place?', 0, 'Your project location', 6, @apc_template_id, @apc_your_funding_section_id, 1, "PROJECT_LOCATION"),
     (NULL, 'Where will most of the project work take place?', 0, 'Your project location', 6, @ati_template_id, @ati_your_funding_section_id, 1, "PROJECT_LOCATION");

SET @programme_your_project_location_section_id = (SELECT id FROM section WHERE competition_id=@programme_template_id AND section_type="PROJECT_LOCATION");
SET @sector_your_project_location_section_id = (SELECT id FROM section WHERE competition_id=@sector_template_id AND section_type="PROJECT_LOCATION");
SET @generic_your_project_location_section_id = (SELECT id FROM section WHERE competition_id=@generic_template_id AND section_type="PROJECT_LOCATION");
SET @apc_your_project_location_section_id = (SELECT id FROM section WHERE competition_id=@apc_template_id AND section_type="PROJECT_LOCATION");
SET @ati_your_project_location_section_id = (SELECT id FROM section WHERE competition_id=@ati_template_id AND section_type="PROJECT_LOCATION");


INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    VALUES
    (false, 'Description not used', true, true, 'Project location question', 'Project location question', '1', @programme_template_id, @programme_your_project_location_section_id, 'GENERAL', NULL),
    (false, 'Description not used', true, true, 'Project location question', 'Project location question', '1', @sector_template_id, @sector_your_project_location_section_id, 'GENERAL', NULL),
    (false, 'Description not used', true, true, 'Project location question', 'Project location question', '1', @generic_template_id, @generic_your_project_location_section_id, 'GENERAL', NULL),
    (false, 'Description not used', true, true, 'Project location question', 'Project location question', '1', @apc_template_id, @apc_your_project_location_section_id, 'GENERAL', NULL),
    (false, 'Description not used', true, true, 'Project location question', 'Project location question', '1', @ati_template_id, @ati_your_project_location_section_id, 'GENERAL', NULL);

