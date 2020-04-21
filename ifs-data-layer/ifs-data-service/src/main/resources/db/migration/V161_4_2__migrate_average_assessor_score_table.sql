
INSERT INTO average_assessor_score
(application_id, score)
SELECT
    p.target_id                                                   AS application_id,
    ROUND(AVG(afir.value / q.assessor_maximum_score) * 100, 1)    AS score
FROM assessor_form_input_response afir
    INNER JOIN process p
    ON p.id = afir.assessment_id
    INNER JOIN form_input fi
    ON fi.id = afir.form_input_id and fi.form_input_type_id=23
    INNER JOIN question q
    ON q.id = fi.question_id
WHERE
p.activity_state_id = 5 -- submitted
GROUP BY p.target_id;
