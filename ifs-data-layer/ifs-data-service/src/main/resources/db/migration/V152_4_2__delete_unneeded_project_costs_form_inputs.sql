ALTER TABLE finance_row
 DROP FOREIGN KEY FK_3ocl28vkv3coj1t5hmgixvl6,
 DROP COLUMN question_id;

-- Project costs
DELETE fir FROM form_input_response fir
    INNER JOIN form_input fi
        ON fi.id = fir.form_input_id
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES';

DELETE fi FROM form_input fi
    INNER JOIN question q
        ON q.id = fi.question_id
    INNER JOIN section s
        ON s.id = q.section_id
    WHERE s.section_type = 'PROJECT_COST_FINANCES';

