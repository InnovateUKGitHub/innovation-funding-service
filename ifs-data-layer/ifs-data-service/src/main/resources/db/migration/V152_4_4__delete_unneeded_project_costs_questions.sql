DELETE q FROM question q
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES'
        AND q.short_name != 'Project finances';