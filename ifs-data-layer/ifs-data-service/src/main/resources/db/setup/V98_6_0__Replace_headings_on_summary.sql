UPDATE question q
SET q.description = REPLACE (q.description, '<h2 class="heading-medium">Funding rules for this competition</h2>', '')
WHERE q.section_id IN (SELECT id FROM section WHERE section_type = 'OVERVIEW_FINANCES');

UPDATE question q
SET q.description = ''
WHERE q.name IS NULL
AND q.section_id IN (SELECT id FROM section WHERE section_type = 'OVERVIEW_FINANCES')
AND q.competition_id IN (SELECT template_competition_id FROM competition_type WHERE template_competition_id IS NOT NULL);