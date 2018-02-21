### Set Programme competition template application question file appendix default guidance ###
# This query is part of IFS-2565.

SET @programme_competition_id = (   SELECT id
                                    FROM competition
                                    WHERE competition.name = 'Template for the Programme competition type'
                                    AND template = true);

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@programme_competition_id )
	INNER JOIN question ON (
		question.short_name='Technical approach'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can include an appendix of additional information to support the technical approach the project will undertake.<br/>This can include for example, a Gantt chart or project management structure.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 6 sides of A4. Longer appendices will only have the first 6 pages assessed</li></ul>'
WHERE form_input.description = 'Appendix';

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@programme_competition_id )
	INNER JOIN question ON (
		question.short_name='Innovation'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can include an appendix of additional information to support your answer. This appendix can include graphics describing the innovation or the nature of the problem. You can include evidence of freedom to operate, patent searches or competitor analysis as supporting information.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>not be any longer than 5 sides of A4. Longer appendices will only have the first 5 pages assessed</li></ul>'
WHERE form_input.description = 'Appendix';

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@programme_competition_id )
	INNER JOIN question ON (
		question.short_name='Project team'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can include an appendix of additional information to detail the specific expertise and track record of each project partner and subcontractor. Academic collaborators can refer to their research standing.</p><p>The appendix should:</p><ul><li>contain your application number and project title at the top</li><li>include up to half an A4 page per partner describing the skills and experience of the main people who will be working on the project</li></ul>'
WHERE form_input.description = 'Appendix';


### Set Sector competition template application question file appendix default guidance ###

SET @sector_competition_id = (   SELECT id
                                    FROM competition
                                    WHERE competition.name = 'Template for the Sector competition type'
                                    AND template = true);

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@sector_competition_id )
	INNER JOIN question ON (
		question.short_name='Approach and innovation'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can submit up to 2 pages to provide graphics, diagrams or an image to explain your innovation.</p>'
WHERE form_input.description = 'Appendix';

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@sector_competition_id )
	INNER JOIN question ON (
		question.short_name='Team and resources'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can submit up to 4 pages to describe the skills and experience of the main people working on the project.</p>'
WHERE form_input.description = 'Appendix';

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@sector_competition_id )
	INNER JOIN question ON (
		question.short_name='Project management'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can submit a project plan or Gantt chart of up to 2 pages.</p>'
WHERE form_input.description = 'Appendix';

UPDATE form_input
    INNER JOIN section ON ( section.competition_id=@sector_competition_id )
	INNER JOIN question ON (
		question.short_name='Risks'
		AND question.id = form_input.question_id
		AND question.section_id = section.id)
SET guidance_answer='<p>You can submit a risk register of up to 2 pages.</p>'
WHERE form_input.description = 'Appendix';