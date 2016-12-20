
-- Update questions in templates in which the description has a wrongly escaped &
UPDATE question q
    INNER JOIN competition c ON q.competition_id=c.id
    SET q.description='<h2 class=\"heading-medium\">Funding rules for this competition</h2><p>We will fund projects between &pound;500,000 and &pound;1.5 million. We may consider projects with costs outside of this range. We expect projects to last between 12 and 36 months.</p><p>Innovate UK\'s aim is to optimise the level of funding businesses receive. We also recognise the importance of research organisations\' contribution to R&amp;D projects. Therefore we require the following levels of participation:</p><ul class=\"list-bullet\"> <li>at least 70% of the total eligible project costs are incurred by commercial organisations and</li> <li>a maximum of 30% of total eligible project costs are available to research participants. Where there is more than one research participant, this maximum will be shared between them.</li></ul>'
WHERE c.template=1
    AND q.description LIKE '<h2 class=\"heading-medium\">Funding rules%';

-- Update all template questions where the description, short_name or name is the same as the live competition.
-- Update the fields to match the live competition.
UPDATE question qt
	INNER JOIN competition c ON qt.competition_id=c.id
    INNER JOIN question ql
    ON ql.competition_id=1
    AND ((qt.name=ql.name AND qt.name IS NOT NULL)
    OR (qt.short_name=ql.short_name AND qt.short_name IS NOT NULL)
    OR (qt.description=ql.description AND qt.description IS NOT NULL)
    OR (qt.description IS NULL AND ql.description IS NULL
    AND qt.name IS NULL AND ql.name IS NULL
    AND qt.short_name IS NULL AND ql.short_name IS NULL))
    SET qt.assign_enabled=ql.assign_enabled,
		qt.description=ql.description,
		qt.mark_as_completed_enabled=ql.mark_as_completed_enabled,
		qt.multiple_statuses=ql.multiple_statuses,
		qt.name=ql.name,
		qt.short_name=ql.short_name,
		qt.priority=ql.priority,
		qt.question_number=ql.question_number,
		qt.assessor_maximum_score=ql.assessor_maximum_score,
		qt.question_type=ql.question_type
WHERE c.template=1;