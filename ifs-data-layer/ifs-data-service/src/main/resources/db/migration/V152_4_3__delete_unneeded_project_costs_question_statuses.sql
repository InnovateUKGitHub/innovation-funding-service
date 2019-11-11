ALTER TABLE section ADD INDEX section_section_type_ix(section_type);
ALTER TABLE question ADD INDEX question_short_name_ix(short_name);

DELETE qs FROM question_status qs
    INNER JOIN question q
        ON q.id = qs.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES'
        AND q.short_name != 'Project finances';