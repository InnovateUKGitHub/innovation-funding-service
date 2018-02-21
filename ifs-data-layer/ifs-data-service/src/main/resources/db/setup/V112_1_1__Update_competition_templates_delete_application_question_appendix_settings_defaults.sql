# Set guidance and allowed file types to null for disabled appendix in all competition templates' default application questions.
# This query is part of IFS-2565.

UPDATE form_input
    INNER JOIN competition ON (competition.template = true)
    INNER JOIN section ON (
        section.competition_id=competition.id
        AND section.name = 'Application questions'
    )
	INNER JOIN question ON (
	    question.section_id = section.id
		AND question.id = form_input.question_id
	)
SET guidance_answer=NULL, allowed_file_types=NULL
WHERE form_input.description = 'Appendix'
AND form_input.active = false;