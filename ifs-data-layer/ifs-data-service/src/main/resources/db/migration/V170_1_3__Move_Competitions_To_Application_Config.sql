INSERT INTO competition_application_config
(temporary_competition_id, maximum_funding_sought)
SELECT c.id AS temporary_competition_id,
null as maximum_funding_sought
FROM competition c
WHERE c.id NOT IN (
SELECT cac.temporary_competition_id FROM competition_application_config cac);

UPDATE competition c
INNER JOIN competition_application_config cag
SET c.competition_application_config_id = cag.id
WHERE cag.temporary_competition_id = c.id;

ALTER TABLE competition_application_config DROP COLUMN temporary_competition_id;