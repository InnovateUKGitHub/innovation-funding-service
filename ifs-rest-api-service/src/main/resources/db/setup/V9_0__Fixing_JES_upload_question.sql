UPDATE question q
    INNER JOIN question_form_input qfi
        ON q.id = qfi.question_id
	INNER JOIN form_input fi
        ON qfi.form_input_id = fi.id 
        SET q.mark_as_completed_enabled = 1
    WHERE
        fi.form_input_type_id = 20;