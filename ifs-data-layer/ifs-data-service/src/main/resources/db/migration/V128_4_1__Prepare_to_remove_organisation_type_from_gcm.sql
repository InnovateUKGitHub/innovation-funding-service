-- IFS-4099
-- Allow organisation_type_id to be null when new rows are created. It's no longer used and will be removed by IFS-4271.
ALTER TABLE grant_claim_maximum
  MODIFY organisation_type_id BIGINT(20) NULL;

-- Delete grant_claim_maximum rows unless they are for the Business organisation type
SET @business_organisation_type_id = (SELECT id FROM organisation_type WHERE name='Business');
DELETE FROM grant_claim_maximum_competition
WHERE grant_claim_maximum_id IN (SELECT id
                                 FROM grant_claim_maximum
                                 WHERE
                                   organisation_type_id <> @business_organisation_type_id);

DELETE FROM grant_claim_maximum WHERE organisation_type_id <> @business_organisation_type_id;