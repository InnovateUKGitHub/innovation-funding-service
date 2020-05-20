INSERT INTO competition_organisation_config
(temporary_competition_id, international_organisations_allowed, international_lead_organisation_allowed)
SELECT c.id AS temporary_competition_id,
null AS international_organisations_allowed,
null AS international_lead_organisation_allowed
FROM competition c
WHERE c.id NOT IN (
	SELECT cog.temporary_competition_id FROM competition_organisation_config cog
);

UPDATE competition c
INNER JOIN competition_organisation_config cog
SET c.competition_organisation_config_id = cog.id
WHERE cog.temporary_competition_id = c.id;

ALTER TABLE competition_organisation_config DROP FOREIGN KEY competition_organisation_config_fk;
ALTER TABLE competition_organisation_config DROP COLUMN temporary_competition_id;