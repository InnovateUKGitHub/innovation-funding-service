INSERT INTO competition_assessment_config
(temporary_competition_id, include_average_assessor_score_in_notifications, has_assessment_panel, has_interview_stage, assessor_count, assessor_pay, assessor_finance_view)
SELECT c.id AS temporary_competition_id,
0 AS include_average_assessor_score_in_notifications,
c.has_assessment_panel as has_assessment_panel,
c.has_interview_stage as has_interview_stage,
c.assessor_count as assessor_count,
c.assessor_pay as assessor_pay,
c.assessor_finance_view as assessor_finance_view
FROM competition c
WHERE c.id NOT IN (
SELECT cac.temporary_competition_id FROM competition_assessment_config cac);

UPDATE competition c
INNER JOIN competition_assessment_config cag
SET c.competition_assessment_config_id = cag.id
WHERE cag.temporary_competition_id = c.id;

ALTER TABLE competition_assessment_config DROP COLUMN temporary_competition_id;