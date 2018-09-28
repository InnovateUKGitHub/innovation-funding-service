-- IFS-4271
-- ZDD cleanup for Grant Claim Maximum
ALTER TABLE grant_claim_maximum
  DROP FOREIGN KEY grant_claim_maximum_organisation_type_fk;

ALTER TABLE grant_claim_maximum DROP COLUMN organisation_type_id;