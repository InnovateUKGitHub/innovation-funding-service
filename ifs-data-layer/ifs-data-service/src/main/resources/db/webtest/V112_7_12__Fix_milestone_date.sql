-- Temporary webtestdata fix. Should be removed when Duncans PR goes in.
SET @photonics_for_health_competition_id = (SELECT id
                                     FROM competition
                                     WHERE competition.name = 'Photonics for health');

UPDATE `milestone`
SET `DATE`='2018-03-14 00:00:00'
WHERE type='OPEN_DATE'
AND competition_id=@photonics_for_health_competition_id;

UPDATE `milestone`
SET `DATE`='2018-03-15 00:00:00'
WHERE type='BRIEFING_EVENT'
AND competition_id=@photonics_for_health_competition_id;