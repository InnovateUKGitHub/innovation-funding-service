-- IFS-5989 -- remove per-organisation t&cs and hide from assessment

SET @eoi_competition_type = (SELECT id FROM competition_type ct WHERE ct.name='Expression of interest');

# -- remove terms from assessment for eoi competitions
# UPDATE section s
#     SET s.display_in_assessment_application_summary = FALSE
# WHERE
#       s.competition_id  IN (SELECT c.id FROM competition c WHERE c.competition_type_id = @eoi_competition_type);

-- we don't want to accept per organisation
UPDATE question q
    SET q.multiple_statuses = FALSE
WHERE
    q.competition_id IN (SELECT c.id FROM competition c WHERE c.competition_type_id = @eoi_competition_type);
