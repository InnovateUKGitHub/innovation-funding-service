-- IFS-7194 migrate competitions to not allow international

INSERT INTO competition_organisation_config
(competition_id, international_organisations_allowed)
SELECT c.id AS competition_id,
false AS international_organisations_allowed
FROM competition c
WHERE c.id NOT IN (
	SELECT cog.competition_id FROM competition_organisation_config cog
);