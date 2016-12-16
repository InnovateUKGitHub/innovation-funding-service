UPDATE form_input f
	LEFT JOIN question q ON f.question_id=q.id
	LEFT JOIN competition c ON q.competition_id=c.id
    SET word_count=400
    WHERE c.template=1 AND f.scope='APPLICATION' AND f.form_input_type_id=2;