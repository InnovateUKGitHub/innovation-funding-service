-- IFS-4110: Script to fix a bug introduced by IFS-2123 that applicants could not choose a research category option
--   on the Application details page, nor from the Application overview menu.
-- This brings 'in-between' competitions up-to-date with the latest view of the application form, which previously had a
-- backwards compatible view.

-- UPDATE COMPETITIONS --

-- 1. Create temporary table for the ids of the affected competitions
CREATE TEMPORARY TABLE competitions_with_team_but_not_research_question (
    id bigint(20)
);

-- 2. Fill it with the ids of competitions that were created after IFS-3088 but before IFS-2123
INSERT INTO competitions_with_team_but_not_research_question 
	SELECT competition_id FROM question
    WHERE question_setup_type="APPLICATION_TEAM"
    AND competition_id NOT IN
        (SELECT competition_id FROM question WHERE question_setup_type="RESEARCH_CATEGORY")
;

-- 3. Bump the priority of all the existing questions to to make space for the 'Research category' question at the top
UPDATE question SET priority = priority + 1
    WHERE competition_id IN (SELECT * FROM competitions_with_team_but_not_research_question)
    AND section_id IN (
        SELECT s.id FROM section s WHERE s.name IN ('Project details', 'Application questions')
    )
;

-- 4. Add the new research category question to the competitions
-- Add research category question with priority 3 for each of the template competitions by copying the application team row with some small changes.
INSERT INTO question (assign_enabled, description, mark_as_completed_enabled, multiple_statuses, name, short_name, priority, competition_id, section_id, question_type, question_setup_type)
    SELECT
        assign_enabled, description, mark_as_completed_enabled, multiple_statuses, 'Research category', 'Research category', '3', competition_id, section_id, question_type, 'RESEARCH_CATEGORY'
    FROM question WHERE competition_id in (SELECT id FROM competitions_with_team_but_not_research_question) 
            AND question_setup_type="APPLICATION_TEAM"
;

-- MARK AS COMPLETE ON APPLICATIONS --

-- 1. Create temporary table for the applications which will need their new question marked as complete immediately
CREATE TEMPORARY TABLE applications_with_category_set_and_details_complete (
    id bigint(20)
);

-- 2. Fill it with the ids of applications where the Application Details page was already marked as complete,
--    but only if they have also chosen a research category (which could have happenned before IFS-2123 was deployed).
--    note: a missing question_status entry is synonymous with 'incomplete'.
INSERT INTO applications_with_category_set_and_details_complete
    SELECT id FROM application a
        -- If the application is in one of the affected competitions...
        WHERE a.competition IN (SELECT id FROM competitions_with_team_but_not_research_question)
        -- ...and has a research category selected
        AND EXISTS (
            SELECT * from category_link where class_pk=a.id AND class_name="org.innovateuk.ifs.application.domain.Application#researchCategory"
        )
        -- ...and has its application details question marked as complete
        AND EXISTS (
            SELECT qs.id from question_status qs JOIN question q ON (qs.question_id=q.id)
            WHERE qs.application_id=a.id
            AND q.question_setup_type="APPLICATION_DETAILS"
            AND qs.marked_as_complete=true
        )
;

-- 3. Add a new question status as a copy of the Application details one, but with the question id of the new research category question.
INSERT INTO question_status (assigned_date, marked_as_complete, notified, application_id, assigned_by_id, assignee_id, marked_as_complete_by_id, question_id)
    SELECT
        assigned_date, marked_as_complete, notified, application_id, assigned_by_id, assignee_id, marked_as_complete_by_id, research_question.id
    FROM question_status qs
        -- Join the question table twice to get to the corresponding research category question
        JOIN question details_question ON (qs.question_id=details_question.id)
        JOIN question research_question ON (details_question.competition_id=research_question.competition_id)
            WHERE application_id IN (SELECT id FROM applications_with_category_set_and_details_complete)
            AND details_question.question_setup_type="APPLICATION_DETAILS"
            AND research_question.question_setup_type="RESEARCH_CATEGORY"
;

-- Finally, delete the temporary tables
DROP TABLE competitions_with_team_but_not_research_question, applications_with_category_set_and_details_complete;
