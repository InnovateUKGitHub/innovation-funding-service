
-- organisation finances
DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'ORGANISATION_FINANCES'
    and fi.form_input_type_id = 19;
