-- IFS-4650 - setting RELEASE_FEEDBACK as the Completion stage of existing Prince's Trust and Expression of Interest
-- competitions and PROJECT_SETUP for the rest.

SET @princes_trust_competition_type_id = (SELECT id FROM competition_type WHERE name="The Prince's Trust");
SET @eoi_competition_type_id =           (SELECT id FROM competition_type WHERE name="Expression of interest");

UPDATE competition SET completion_stage = 'RELEASE_FEEDBACK'
WHERE competition_type_id IN (@princes_trust_competition_type_id, @eoi_competition_type_id);

UPDATE competition SET completion_stage = 'PROJECT_SETUP'
WHERE competition_type_id NOT IN (@princes_trust_competition_type_id, @eoi_competition_type_id);