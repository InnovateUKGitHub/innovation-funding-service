-- Temporary Web Test data fix. Should be removed when TODO IFS-2986.

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


--
SET @photonics_for_research_competition_id = (SELECT id
                                              FROM competition
                                              WHERE competition.name = 'Photonics for Research');


UPDATE `milestone`
SET `DATE`='2018-03-15 00:00:00'
WHERE type='BRIEFING_EVENT'
AND competition_id=@photonics_for_research_competition_id;


--
SET @photonics_for_public_competition_id = (SELECT id
                                            FROM competition
                                            WHERE competition.name = 'Photonics for Public');


UPDATE `milestone`
SET `DATE`='2018-03-15 00:00:00'
WHERE type='BRIEFING_EVENT'
AND competition_id=@photonics_for_public_competition_id;


--
SET @photonics_for_rto_competition_id = (SELECT id
                                         FROM competition
                                         WHERE competition.name = 'Photonics for RTO');


UPDATE `milestone`
SET `DATE`='2018-03-15 00:00:00'
WHERE type='BRIEFING_EVENT'
AND competition_id=@photonics_for_rto_competition_id;


--
SET @photonics_for_all_competition_id = (SELECT id
                                         FROM competition
                                         WHERE competition.name = 'Photonics for All');


UPDATE `milestone`
SET `DATE`='2018-03-15 00:00:00'
WHERE type='BRIEFING_EVENT'
AND competition_id=@photonics_for_all_competition_id;