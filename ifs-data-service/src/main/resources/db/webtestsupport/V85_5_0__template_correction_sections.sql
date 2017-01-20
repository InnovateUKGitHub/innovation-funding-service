-- Update all template questions where the description, short_name or name is the same as the live competition.
-- Update the fields to match the live competition.
UPDATE section st
	INNER JOIN competition c ON st.competition_id=c.id
    INNER JOIN section sl
    ON sl.competition_id=1
    AND st.name=sl.name
    SET st.priority=sl.priority,
		st.section_type=sl.section_type,
		st.display_in_assessment_application_summary=sl.display_in_assessment_application_summary,
		st.question_group=sl.question_group,
		st.description=sl.description,
		st.assessor_guidance_description=sl.assessor_guidance_description
WHERE c.template=1;