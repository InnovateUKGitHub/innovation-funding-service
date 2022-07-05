INSERT INTO competition_horizon_work_programme (competition_id, work_programme_id)
SELECT c.id, hwp.id FROM competition c, horizon_work_programme hwp WHERE competition_type_id IN (
    SELECT id FROM competition_type WHERE name IN ('Horizon Europe Guarantee')
) ORDER BY c.id, hwp.id;