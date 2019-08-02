-- IFS-6186 Template form input type

-- These form input types are no longer needed.
DELETE FROM form_input_type WHERE id in (1, 3, 18);

-- Appendix shouldn't exist for your organisation
DELETE aft FROM appendix_file_types aft
    INNER JOIN form_input fi
        ON fi.id = aft.form_input_id
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON q.section_id = s.id
    WHERE s.section_type = 'ORGANISATION_FINANCES'
        AND fi.form_input_type_id = 4;

DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON q.section_id = s.id
    WHERE s.section_type = 'ORGANISATION_FINANCES'
        AND fi.form_input_type_id = 4;