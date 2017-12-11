SET @programme_competition_type_id=(SELECT id FROM competition_type where name = 'Programme');
SET @apc_competition_type_id=(SELECT id FROM competition_type where name = 'Advanced Propulsion Centre');


INSERT INTO grant_claim_maximum (category_id, organisation_size_id, organisation_type_id, competition_type_id, maximum)
  SELECT category_id, organisation_size_id, organisation_type_id, @apc_competition_type_id, maximum
  FROM grant_claim_maximum
  WHERE competition_type_id = @programme_competition_type_id;