-- IFS-6673 H2020 terms and conditions

-- add the new terms
INSERT INTO terms_and_conditions (id, name, template, version, type, created_by, created_on, modified_on, modified_by)
VALUES (24, 'H2020', 'h2020-terms-and-conditions', 1, 'GRANT', 15, now(), now(), 15);

SET @h2020_competition_type = (SELECT id FROM competition_type WHERE name = 'Horizon 2020');

-- update all h2020 competitions to use the new terms
UPDATE competition
  SET terms_and_conditions_id = 24
WHERE competition_type_id = @h2020_competition_type;