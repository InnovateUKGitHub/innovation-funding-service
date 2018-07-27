-- IFS-3818 Add the new Grant Claim Maximum many to many join table

-- Remove some spurious duplicates in grant claim maximum which violate the unique row constraint
-- They can be seen by executing this query:
-- SELECT
--   id,
--   competition_type_id,
--   category_id,
--   organisation_type_id,
--   organisation_size_id,
--   maximum
-- FROM
--   grant_claim_maximum
-- WHERE organisation_size_id IS NULL
-- ORDER BY competition_type_id, category_id, organisation_type_id, id;

DELETE FROM
  grant_claim_maximum using grant_claim_maximum,
  grant_claim_maximum gcm
WHERE grant_claim_maximum.id > gcm.id
      AND grant_claim_maximum.competition_type_id = gcm.competition_type_id
      AND grant_claim_maximum.category_id = gcm.category_id
      AND grant_claim_maximum.organisation_type_id = gcm.organisation_type_id
      AND grant_claim_maximum.organisation_size_id IS NULL;

-- Create the new many-to-many relationship
CREATE TABLE grant_claim_maximum_competition
(
  `competition_id` bigint(20) NOT NULL,
  `grant_claim_maximum_id` bigint(20) NOT NULL,
  PRIMARY KEY (`competition_id`,`grant_claim_maximum_id`),
  CONSTRAINT grant_claim_maximum_competition_competition_fk FOREIGN KEY (competition_id) REFERENCES competition (id),
  CONSTRAINT grant_claim_maximum_competition_grant_claim_maximum_fk FOREIGN KEY (grant_claim_maximum_id) REFERENCES
  grant_claim_maximum (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert grant claim maximum competition values for all of the template competitions
INSERT INTO grant_claim_maximum_competition (competition_id, grant_claim_maximum_id)
SELECT  ct.template_competition_id, gcm.id
FROM
   grant_claim_maximum gcm
INNER JOIN competition_type ct ON gcm.competition_type_id = ct.id
WHERE template_competition_id IS NOT null
ORDER BY ct.template_competition_id, gcm.id;

-- Insert grant claim maximum competition values for all of the non-template competitions
INSERT INTO grant_claim_maximum_competition (competition_id, grant_claim_maximum_id)
SELECT  c.id, gcm.id
FROM
  competition c
  INNER JOIN grant_claim_maximum gcm ON c.competition_type_id = gcm.competition_type_id
ORDER BY c.id, gcm.id;
