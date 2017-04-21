UPDATE question q
INNER JOIN section s
ON q.competition_id=s.competition_id
SET q.section_id=s.id
WHERE q.short_name='Je-s Output'
AND s.name='Your project costs';