-- IFS-5874

-- Update questions that use the cost category type sections to use the 'Your project costs'.
UPDATE question q
INNER JOIN section current_section ON q.section_id=current_section.id
SET q.section_id = (SELECT s.id FROM section s WHERE s.competition_id=q.competition_id AND s.section_type='PROJECT_COST_FINANCES')
WHERE current_section.name IN ('Labour',
'Overhead costs',
'Materials',
'Capital usage',
'Subcontracting costs',
'Travel and subsistence',
'Other costs');

-- delete the cost category type sections.
DELETE FROM section WHERE name IN  ('Labour',
'Overhead costs',
'Materials',
'Capital usage',
'Subcontracting costs',
'Travel and subsistence',
'Other costs');

-- Delete form inputs from the guidance questions
DELETE fi FROM form_input AS fi
INNER JOIN question q ON q.id = fi.question_id
INNER JOIN section s ON s.id = q.section_id
WHERE s.section_type='PROJECT_COST_FINANCES' AND q.name IS NULL;

-- Delete question statuses from the guidance questions
DELETE qs FROM question_status AS qs
INNER JOIN question q ON q.id = qs.question_id
INNER JOIN section s ON s.id = q.section_id
WHERE s.section_type='PROJECT_COST_FINANCES' AND q.name IS NULL;

-- Delete the guidance questions
DELETE q FROM question AS q
INNER JOIN section s ON s.id = q.section_id
WHERE s.section_type='PROJECT_COST_FINANCES' AND q.name IS NULL;
