DELETE FROM form_input
WHERE description = 'appendix'
AND competition_id in (2,3)
AND question_id IN
	(SELECT question_id
	FROM (SELECT * FROM form_input) as form_input_sub
	WHERE description = 'Project Summary');

DELETE FROM form_input
WHERE description = 'appendix'
AND competition_id in (2,3)
AND question_id IN
	(SELECT question_id
	FROM (SELECT * FROM form_input) as form_input_sub
	WHERE description = 'Public description');

DELETE FROM form_input
WHERE description = 'appendix'
AND competition_id in (2,3)
AND question_id IN
	(SELECT question_id
	FROM (SELECT * FROM form_input) as form_input_sub
	WHERE description = 'How does your project align with the scope of this competition?');