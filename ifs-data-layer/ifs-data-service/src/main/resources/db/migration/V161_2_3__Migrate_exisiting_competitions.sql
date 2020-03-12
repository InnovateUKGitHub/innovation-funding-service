-- IFS-7194 migrate competitions to not allow international

SELECT c.id AS competition_id,
false AS international_organisations_allowed
FROM competition c
WHERE c.id NOT IN (
	SELECT cog.competition_id FROM competition_organisation_config cog
)
ORDER BY c.id;
