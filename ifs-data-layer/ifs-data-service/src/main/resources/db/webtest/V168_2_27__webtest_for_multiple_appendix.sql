UPDATE form_input fi

inner join competition comp
    on comp.id = fi.competition_id
inner join question q
    on q.id = fi.question_id

SET word_count = 5

WHERE comp.name='Predicting market trends programme'
AND q.short_name='Project team'
AND fi.form_input_type_id=4;
