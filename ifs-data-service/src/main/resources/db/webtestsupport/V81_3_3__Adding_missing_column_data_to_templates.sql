SET @programme_competition_id = (SELECT `template_competition_id` FROM `competition_type` WHERE name='Programme');
SET @sector_competition_id = (SELECT `template_competition_id` FROM `competition_type` WHERE name='Sector');

-- Populate missing competition_id column
UPDATE question q
INNER JOIN section s ON
    q.section_id=s.id
SET q.competition_id = s.competition_id
WHERE (s.competition_id=@programme_competition_id OR s.competition_id=@sector_competition_id);

-- Copy multiple_statuses and question_type columns from the original competition.
UPDATE question q
    INNER JOIN question qs ON
    (q.description=qs.description or q.short_name=qs.short_name OR q.name=qs.name)
SET q.multiple_statuses=qs.multiple_statuses,q.question_type=qs.question_type
WHERE (q.competition_id=@programme_competition_id OR q.competition_id=@sector_competition_id)
AND (qs.competition_id=1);