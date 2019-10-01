-- Project costs
DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES';

DELETE qs FROM question_status qs
    INNER JOIN question q
        ON q.id = qs.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES'
        AND q.short_name != 'Project finances';

DELETE q FROM question q
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES'
        AND q.short_name != 'Project finances';

-- Funding
DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'FUNDING_FINANCES';

DELETE qs FROM question_status qs
    INNER JOIN question q
        ON q.id = qs.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'FUNDING_FINANCES'
        AND q.short_name != 'Funding level';

DELETE q FROM question q
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'FUNDING_FINANCES'
        AND q.short_name != 'Funding level';

-- Overview
DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'OVERVIEW_FINANCES'
    AND fi.form_input_type_id != 16;

DELETE qs FROM question_status qs
    INNER JOIN question q
        ON q.id = qs.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'OVERVIEW_FINANCES'
        AND q.name != 'FINANCE_SUMMARY_INDICATOR_STRING';

DELETE q FROM question q
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'OVERVIEW_FINANCES'
        AND q.name != 'FINANCE_SUMMARY_INDICATOR_STRING';

-- organisation finances
DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'ORGANISATION_FINANCES'
    and fi.form_input_type_id = 19;



