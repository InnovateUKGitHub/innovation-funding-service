-- IFS-7370: Make assessor score available in notifications: Domain for storing assessor scores & emails

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
    INNER JOIN milestone assessment_milestone
    on q.competition_id = assessment_milestone.competition_id and assessment_milestone.type='ASSESSORS_NOTIFIED'
    INNER JOIN milestone notifications_milestone
    on q.competition_id = notifications_milestone.competition_id and notifications_milestone.type='NOTIFICATIONS'
WHERE
        p.activity_state_id = 5 -- submitted
AND     assessment_milestone.date < now()
AND     notifications_milestone.date > now()
GROUP BY p.target_id;
